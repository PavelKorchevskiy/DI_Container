import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dao.A;
import dao.AI;
import dao.B;
import dao.BI;
import dao.C;
import dao.CI;
import dao.DI;
import dao.DWithTooManyConstructors;
import dao.DWithoutConstructor;
import org.container.exceptions.BindingNotFoundException;
import org.container.exceptions.ConstructorNotFoundException;
import org.container.exceptions.TooManyConstructorsException;
import org.container.injector.Injector;
import org.container.injector.InjectorImpl;
import org.container.provider.Provider;
import org.junit.jupiter.api.Test;

public class DITest {

  @Test
  public void testDI() {
    Injector injector = new InjectorImpl();
    injector.bind(AI.class, A.class);
    injector.bind(BI.class, B.class);
    injector.bind(CI.class, C.class);
    Provider<AI> provider = injector.getProvider(AI.class);
    System.out.println(provider.getInstance());
  }

  @Test
  public void constructorNotFoundAssert() {
    Injector injector = new InjectorImpl();
    injector.bind(DI.class, DWithoutConstructor.class);
    assertThrows(ConstructorNotFoundException.class, () -> injector.getProvider(DI.class));
  }

  @Test
  public void tooManyConstructorsAssert() {
    Injector injector = new InjectorImpl();
    injector.bind(DI.class, DWithTooManyConstructors.class);
    injector.bind(BI.class, B.class);
    injector.bind(CI.class, C.class);
    assertThrows(TooManyConstructorsException.class, () -> injector.getProvider(DI.class));
  }

  @Test
  public void bindingNotFoundAssert() {
    Injector injector = new InjectorImpl();
    injector.bind(DI.class, DWithTooManyConstructors.class);
    injector.bind(BI.class, B.class);
    assertThrows(BindingNotFoundException.class, () -> injector.getProvider(DI.class));
  }

  @Test
  public void nullIfBindingNotFound() {
    Injector injector = new InjectorImpl();
    DI object = injector.getProvider(DI.class).getInstance();
    assertNull(object);
  }

  @Test
  public void prototype() {
    Injector injector = new InjectorImpl();
    injector.bind(AI.class, A.class);
    injector.bind(BI.class, B.class);
    injector.bind(CI.class, C.class);
    Provider<AI> firstProvider = injector.getProvider(AI.class);
    Provider<AI> secondProvider = injector.getProvider(AI.class);
    AI first = firstProvider.getInstance();
    AI second = secondProvider.getInstance();
    assertNotEquals(first, second);
  }

  @Test
  public void singleton() {
    Injector injector = new InjectorImpl();
    injector.bindSingleton(AI.class, A.class);
    injector.bindSingleton(BI.class, B.class);
    injector.bindSingleton(CI.class, C.class);
    Provider<AI> firstProvider = injector.getProvider(AI.class);
    Provider<AI> secondProvider = injector.getProvider(AI.class);
    AI first = firstProvider.getInstance();
    AI second = secondProvider.getInstance();
    assertEquals(first, second);
  }

  //если забиндить и как сингалтон и как прототайп
  //сингалтон будет в приоритете
  @Test
  public void singletonInPriority() {
    Injector injector = new InjectorImpl();
    injector.bind(AI.class, A.class);
    injector.bind(BI.class, B.class);
    injector.bind(CI.class, C.class);
    injector.bindSingleton(AI.class, A.class);
    injector.bindSingleton(BI.class, B.class);
    injector.bindSingleton(CI.class, C.class);
    Provider<AI> firstProvider = injector.getProvider(AI.class);
    Provider<AI> secondProvider = injector.getProvider(AI.class);
    AI first = firstProvider.getInstance();
    AI second = secondProvider.getInstance();
    assertEquals(first, second);
  }

}
