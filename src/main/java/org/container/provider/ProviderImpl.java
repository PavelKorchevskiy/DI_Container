package org.container.provider;

public class ProviderImpl<T> implements Provider<T> {

  private T instance;

  public ProviderImpl(T instance) {
    this.instance = instance;
  }

  @Override
  public synchronized T getInstance() {
    return instance;
  }
}
