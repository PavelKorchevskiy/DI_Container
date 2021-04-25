import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dao.Container;
import dao.ContainerWrapper;
import dao.DAO;
import dao.DAOImplWithTooManyConstructors;
import dao.DAOImplWithoutDefaultConstructor;
import dao.EventDAO;
import dao.EventDAOContainer;
import dao.EventDAOImpl;
import dao.Wrapper;
import org.container.exceptions.BindingNotFoundException;
import org.container.exceptions.ConstructorNotFoundException;
import org.container.exceptions.TooManyConstructorsException;
import org.container.injector.Injector;
import org.container.injector.InjectorImpl;
import org.container.provider.Provider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DITest {

  @Test
  void testExistingBinding() {
    Injector injector = new InjectorImpl();
    injector.bind(EventDAO.class, EventDAOImpl.class);
    Provider<EventDAO> daoProvider = injector.getProvider(EventDAO.class);
    assertNotNull(daoProvider);
    assertNotNull(daoProvider.getInstance());
    Assertions.assertSame(EventDAOImpl.class, daoProvider.getInstance().getClass());
  }

  @Test
  public void testConstructorNotFoundAssert() {
    Injector injector = new InjectorImpl();
    injector.bind(DAO.class, DAOImplWithoutDefaultConstructor.class);
    assertThrows(ConstructorNotFoundException.class, () -> injector.getProvider(DAO.class));
  }

  @Test
  public void testTooManyConstructorsAssert() {
    Injector injector = new InjectorImpl();
    injector.bind(DAO.class, DAOImplWithTooManyConstructors.class);
    injector.bind(Container.class, EventDAOContainer.class);
    injector.bind(EventDAO.class, EventDAOImpl.class);
    assertThrows(TooManyConstructorsException.class, () -> injector.getProvider(DAO.class));
  }

  @Test
  public void testBindingNotFoundAssert() {
    Injector injector = new InjectorImpl();
    injector.bind(DAO.class, DAOImplWithTooManyConstructors.class);
    injector.bind(Container.class, EventDAOContainer.class);
    assertThrows(BindingNotFoundException.class, () -> injector.getProvider(DAO.class));
  }

  @Test
  public void testInstanceIsNullIfBindingNotFound() {
    Injector injector = new InjectorImpl();
    DAO object = injector.getProvider(DAO.class).getInstance();
    assertNull(object);
  }

  @Test
  public void testReturningNewInstanceIfBindingIsPrototype() {
    Injector injector = new InjectorImpl();
    injector.bind(Wrapper.class, ContainerWrapper.class);
    injector.bind(Container.class, EventDAOContainer.class);
    injector.bind(EventDAO.class, EventDAOImpl.class);
    Provider<Wrapper> firstProvider = injector.getProvider(Wrapper.class);
    Provider<Wrapper> secondProvider = injector.getProvider(Wrapper.class);
    Wrapper first = firstProvider.getInstance();
    Wrapper second = secondProvider.getInstance();
    assertNotEquals(first, second);
  }

  @Test
  public void testReturningExistingInstanceIfBindingIsSingleton() {
    Injector injector = new InjectorImpl();
    injector.bindSingleton(Wrapper.class, ContainerWrapper.class);
    injector.bindSingleton(Container.class, EventDAOContainer.class);
    injector.bindSingleton(EventDAO.class, EventDAOImpl.class);
    Provider<Wrapper> firstProvider = injector.getProvider(Wrapper.class);
    Provider<Wrapper> secondProvider = injector.getProvider(Wrapper.class);
    Wrapper first = firstProvider.getInstance();
    Wrapper second = secondProvider.getInstance();
    assertEquals(first, second);
  }

  @Test
  public void testSingletonBindingPriorityIfBothArePresent() {
    Injector injector = new InjectorImpl();
    injector.bind(Wrapper.class, ContainerWrapper.class);
    injector.bind(Container.class, EventDAOContainer.class);
    injector.bind(EventDAO.class, EventDAOImpl.class);
    injector.bindSingleton(Wrapper.class, ContainerWrapper.class);
    injector.bindSingleton(Container.class, EventDAOContainer.class);
    injector.bindSingleton(EventDAO.class, EventDAOImpl.class);
    Provider<Wrapper> firstProvider = injector.getProvider(Wrapper.class);
    Provider<Wrapper> secondProvider = injector.getProvider(Wrapper.class);
    Wrapper first = firstProvider.getInstance();
    Wrapper second = secondProvider.getInstance();
    assertEquals(first, second);
  }
}
