package dao;

public class DAOImplWithoutDefaultConstructor implements DAO {

  private String name;

  public DAOImplWithoutDefaultConstructor(String name) {
    this.name = name;
  }
}
