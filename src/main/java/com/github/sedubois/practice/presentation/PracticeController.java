package com.github.sedubois.practice.presentation;

import com.github.sedubois.practice.Practice;
import com.github.sedubois.practice.service.PracticeService;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.vertx.core.Vertx.currentContext;
import static io.vertx.core.json.Json.decodeValue;
import static io.vertx.core.json.Json.encodePrettily;
import static io.vertx.ext.web.Router.router;

@Singleton
public class PracticeController {

  private final PracticeService service;

  @Inject
  PracticeController(PracticeService service) {
    this.service = service;
  }

  public Router getRouter() {
    Router router = router(currentContext().owner());
    router.route().consumes("application/json");
    router.route().produces("application/json");
    router.route("/practices*").handler(BodyHandler.create());
    router.get("/practices").handler(this::get);
    router.put("/practices").handler(this::update);
    return router;
  }

  private void get(RoutingContext routingContext) {
    Practice practice = service.get();
    routingContext.response()
        .setStatusCode(200)
        .end(encodePrettily(practice));
  }

  private void update(RoutingContext routingContext) {
    Practice practice = decodeValue(routingContext.getBodyAsString(), Practice.class);
    practice = service.update(practice);
    routingContext.response()
        .setStatusCode(200)
        .end(encodePrettily(practice));
  }
}