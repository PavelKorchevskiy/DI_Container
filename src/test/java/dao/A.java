package dao;

import org.container.annotations.Inject;

public class A implements AI {

  BI bi;

  public A() {
  }
@Inject
  public A(BI bi) {
    this.bi = bi;
  }

  @Override
  public String toString() {
    return "A{" +
        "bi=" + bi +
        '}';
  }
}
