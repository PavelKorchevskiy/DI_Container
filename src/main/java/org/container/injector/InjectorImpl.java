package org.container.injector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.container.annotations.Inject;
import org.container.exceptions.BindingNotFoundException;
import org.container.exceptions.ConstructorNotFoundException;
import org.container.exceptions.TooManyConstructorsException;
import org.container.provider.Provider;
import org.container.provider.ProviderImpl;

public class InjectorImpl implements Injector {

  final private Map<Class<?>, Class<?>> mapForPrototype = new ConcurrentHashMap<>();
  final private Map<Class<?>, Class<?>> mapForSingleton = new ConcurrentHashMap<>();
  final private Map<Class<?>, Object> mapSingletonInstances = new ConcurrentHashMap<>();

  @Override
  public synchronized <T> Provider<T> getProvider(Class<T> type) {
    return new ProviderImpl<>((T) createInstanceFromClassInterface(type));
  }

  @Override
  public <T> void bind(Class<T> intf, Class<? extends T> impl) {
    mapForPrototype.put(intf, impl);
  }

  @Override
  public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
    mapForSingleton.put(intf, impl);
  }

  private Object createInstanceFromClassInterface(Class<?> clazzI) {
    if (mapForSingleton.containsKey(clazzI)) {
      Class<?> classSingleton = mapForSingleton.get(clazzI);
      Object instanceSingleton = mapSingletonInstances.get(classSingleton);
      if (instanceSingleton == null) {
        synchronized (mapSingletonInstances) {
          instanceSingleton = createInstanceFromClass(classSingleton);
          mapSingletonInstances.put(classSingleton, instanceSingleton);
        }
      }
      return instanceSingleton;
    }
    if (mapForPrototype.containsKey(clazzI)) {
      return createInstanceFromClass(mapForPrototype.get(clazzI));
    }
    return null;
  }

  private Object createInstanceFromClass(Class<?> clazz) {
    Object instance = null;
    Constructor<?>[] constructors = clazz.getDeclaredConstructors();
    for (Constructor<?> constructor : constructors) {
      if (constructor.isAnnotationPresent(Inject.class)) {
        if (instance == null) {
          instance = getInstanceFromAnnotatedConstructor(constructor);
        } else {
          throw new TooManyConstructorsException();
        }
      }
    }
    if (instance == null) {
      try {
        Constructor<?> constructor = clazz.getConstructor();
        instance = constructor.newInstance();
      } catch (NoSuchMethodException e) {
        throw new ConstructorNotFoundException();
      } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
    return instance;
  }

  private Object getInstanceFromAnnotatedConstructor(Constructor<?> constructor) {
    try {
      Object[] args = getArgs(constructor);
      if (Arrays.stream(args).anyMatch(Objects::isNull)) {
        throw new BindingNotFoundException();
      }
      return constructor.newInstance(args);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private Object[] getArgs(Constructor<?> constructor) {
    Class<?>[] parameterTypes = constructor.getParameterTypes();
    return Arrays.stream(parameterTypes)
        .map(this::createInstanceFromClassInterface)
        .toArray();
  }
}
