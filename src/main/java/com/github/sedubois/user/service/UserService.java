package com.github.sedubois.user.service;

import com.github.sedubois.user.User;
import io.vertx.core.json.Json;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static io.vertx.core.Vertx.currentContext;

@Singleton
public class UserService {

  private Map<Long, User> users = new HashMap<>();
  private AtomicLong USER_ID_GENERATOR = new AtomicLong();

  @Inject
  UserService() {}

  public User create() {
    User user = new User(USER_ID_GENERATOR.getAndIncrement());
    addUser(user);
    return user;
  }

  private void addUser(User user) {
    this.users.put(user.getId(), user);
    String serializedUser = Json.encode(user);
    System.out.println("Publishing user app.users.new=" + serializedUser + " to eventBus");
    currentContext().owner().eventBus().publish("app.users.new", serializedUser);
  }

  public User get(long id) {
    return users.get(id);
  }
}
