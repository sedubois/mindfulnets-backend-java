package com.github.sedubois.user.presentation;

import com.github.sedubois.practice.Practice;
import com.github.sedubois.practice.service.PracticeService;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.sedubois.user.User;
import com.github.sedubois.user.service.UserService;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import static io.vertx.core.Vertx.currentContext;
import static io.vertx.core.json.Json.encodePrettily;
import static java.lang.Integer.parseInt;

@Singleton
public class UserController {

  private final UserService service;

  @Inject
  UserController(UserService service) {
    this.service = service;
  }

  public Router getRouter() {
    Router router = Router.router(currentContext().owner());
    router.route().consumes("application/json");
    router.route().produces("application/json");
    router.route("/users*").handler(BodyHandler.create());
    router.post("/users").handler(this::create);
    router.get("/users/:id").handler(this::get);
    return router;
  }

  private void create(RoutingContext routingContext) {
    System.out.println("Received request to create new user");
    User user = service.create();
    routingContext.response()
        .setStatusCode(200)
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(encodePrettily(user));
  }

  private void get(RoutingContext routingContext) {
    String id = routingContext.request().getParam("id");
    User user = service.get(parseInt(id));
    routingContext.response().setStatusCode(200)
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(encodePrettily(user));
  }
}