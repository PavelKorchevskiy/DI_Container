package org.container.injector;

import org.container.provider.Provider;

public interface Injector {

  //получение инстанса класса со всеми иньекциями по классу интерфейса
  <T> Provider<T> getProvider(Class<T> type);

  //регистрация байндинга по классу интерфейса и его реализации
  <T> void bind(Class<T> intf, Class<? extends T> impl);

  //регистрация синглтон класса
  <T> void bindSingleton(Class<T> intf, Class<? extends T> impl);
}
