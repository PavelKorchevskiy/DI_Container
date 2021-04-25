package dao;

import org.container.annotations.Inject;

public class ContainerWrapper implements Wrapper {

  Container container;

  public ContainerWrapper() {
  }

  @Inject
  public ContainerWrapper(Container container) {
    this.container = container;
  }
}
