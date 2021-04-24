package dao;

import org.container.annotations.Inject;

public class DWithTooManyConstructors implements DI {

  private BI bi;

  @Inject
  public DWithTooManyConstructors(BI bi) {
    this.bi = bi;
  }
  @Inject
  public DWithTooManyConstructors() {
  }
}
