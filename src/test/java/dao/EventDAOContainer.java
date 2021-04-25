package dao;

import org.container.annotations.Inject;

public class EventDAOContainer implements Container {

  EventDAO eventDAO;

  @Inject
  public EventDAOContainer(EventDAO eventDAO) {
    this.eventDAO = eventDAO;
  }
}
