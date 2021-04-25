package dao;

import org.container.annotations.Inject;

public class DAOImplWithTooManyConstructors implements DAO {

  private Container container;

  @Inject
  public DAOImplWithTooManyConstructors(Container container) {
    this.container = container;
  }

  @Inject
  public DAOImplWithTooManyConstructors() {
  }
}
