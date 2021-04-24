package org.container.injector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
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

  final private Map<Class<?>, Class<?>> map = new ConcurrentHashMap<>();
  final private Map<Class<?>, Class<?>> mapForSingleton = new ConcurrentHashMap<>();
  final private Map<Class<?>, Object> mapSingletonInstances = new ConcurrentHashMap<>();

  //получение инстанса класса со всеми иньекциями по классу интерфейса
  @Override
  public <T> Provider<T> getProvider(Class<T> type) {
    return new ProviderImpl<T>((T) createInstanceFromClassInterface(type));
  }

  //регистрация байндинга по классу интерфейса и его реализации
  @Override
  public <T> void bind(Class<T> intf, Class<? extends T> impl) {
    map.put(intf, impl);
  }

  //регистрация синглтон класса
  @Override
  public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
    mapForSingleton.put(intf, impl);
  }

  private Object createInstanceFromClassInterface(Class<?> clazzI) {
    Class<?> classSingleton = mapForSingleton.get(clazzI);
    if (classSingleton != null) {
      Object instanceSingleton = mapSingletonInstances.get(classSingleton);
      if (instanceSingleton == null) {
        instanceSingleton = createInstanceFromClass(classSingleton);
        mapSingletonInstances.put(classSingleton, instanceSingleton);
        return instanceSingleton;
      }
      return instanceSingleton;
    }
    //
    Class<?> clazz = map.get(clazzI);
    if (clazz == null) {
      return null;
    }
    return createInstanceFromClass(clazz);
//    Object instance = null;
//    //берем все конструкторы и ищем отмеченные анотацией
//    Constructor<?>[] constructors = clazz.getDeclaredConstructors();
//    for (Constructor<?> constructor : constructors) {
//      if (constructor.isAnnotationPresent(Inject.class)) {
//        //если несколько таких конструкторов - исключение
//        if (instance != null) {
//          throw new TooManyConstructorsException();
//        }
//        instance = getInstanceFromAnnotatedConstructor(constructor);
//      }
//    }
//    //если нет таких конструкторов пытаемся использовать конструктор без параметров
//    // и если такого нет - исключение
//    if (instance == null) {
//      try {
//        Constructor<?> constructor = clazz.getConstructor();
//        instance = constructor.newInstance();
//      } catch (NoSuchMethodException e) {
//        throw new ConstructorNotFoundException();
//      } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
//        throw new RuntimeException(e);
//      }
//    }
//    return instance;
  }

  private Object createInstanceFromClass(Class<?> clazz) {
    Object instance = null;
    //берем все конструкторы и ищем отмеченные анотацией
    Constructor<?>[] constructors = clazz.getDeclaredConstructors();
    for (Constructor<?> constructor : constructors) {
      if (constructor.isAnnotationPresent(Inject.class)) {
        //если несколько таких конструкторов - исключение
        if (instance != null) {
          throw new TooManyConstructorsException();
        }
        instance = getInstanceFromAnnotatedConstructor(constructor);
      }
    }
    //если нет таких конструкторов пытаемся использовать конструктор без параметров
    // и если такого нет - исключение
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
        //если находим конструктор с аннотацией - сразу создаем объект
        //аргументы создаем рекурсивно вызывая этот метод
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
    int length = parameterTypes.length;
    Object[] args = new Object[length];
    for (int i = 0; i < length; i++) {
      args[i] = createInstanceFromClassInterface(parameterTypes[i]);
    }
    return args;
  }

}
