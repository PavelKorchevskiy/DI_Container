package dao;

import org.container.annotations.Inject;

public class B implements BI {

  CI ci;

  @Inject
  public B(CI ci) {
    this.ci = ci;
  }

  @Override
  public String toString() {
    return "B{" +
        "ci=" + ci +
        '}';
  }
}
