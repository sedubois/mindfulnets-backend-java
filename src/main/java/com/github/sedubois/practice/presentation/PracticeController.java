package com.github.sedubois.practice.presentation;

import com.github.sedubois.practice.Practice;
import com.github.sedubois.practice.service.PracticeService;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

@Singleton
public class PracticeController {

  private final PracticeService service;

  @Inject
  PracticeController(PracticeService service) {
    this.service = service;
  }

  public Router getRouter() {
    Router router = Router.router(Vertx.currentContext().owner());
    router.route().consumes("application/json");
    router.route().produces("application/json");
    router.route("/practices*").handler(BodyHandler.create());
    router.put("/practices").handler(this::update);
    return router;
  }

  private void update(RoutingContext routingContext) {
    Practice practice = Json.decodeValue(routingContext.getBodyAsString(), Practice.class);
    System.out.println("Received update request: " + practice);
    practice = service.update(practice);
    routingContext.response()
        .setStatusCode(200)
        .end(Json.encodePrettily(practice));
  }
}