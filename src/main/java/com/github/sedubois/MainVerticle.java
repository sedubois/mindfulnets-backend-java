package com.github.sedubois;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;
import static java.util.Optional.ofNullable;

public class MainVerticle extends AbstractVerticle {

  private static final int PORT = parseInt(ofNullable(getenv("PORT")).orElse("3001"));

  @Override
  public void start() throws Exception {
    vertx
        .createHttpServer()
        .requestHandler(router()::accept)
        .listen(PORT);
  }

  private Router router() {
    Router router = Router.router(vertx);
    router.route().handler(LoggerHandler.create());
    router.route().failureHandler(ErrorHandler.create(true));
    router.mountSubRouter("/api", apiRouter());
    router.route("/eventbus/*").handler(eventBusHandler());
    return router;
  }

  private Router apiRouter() {
    Router router = Router.router(vertx);
    router.route().handler(CorsHandler.create("*")
        .allowedMethod(HttpMethod.GET)
        .allowedMethod(HttpMethod.POST)
        .allowedMethod(HttpMethod.PUT)
        .allowedMethod(HttpMethod.DELETE)
        .allowedMethod(HttpMethod.OPTIONS)
        .allowedHeader("Content-Type"));
    App app = com.github.sedubois.DaggerApp.create();
    router.mountSubRouter("/", app.practiceController().getRouter());
    router.mountSubRouter("/", app.userController().getRouter());
    return router;
  }

  private SockJSHandler eventBusHandler() {
    BridgeOptions options = new BridgeOptions()
        .addOutboundPermitted(new PermittedOptions().setAddressRegex("app..*"));
    return SockJSHandler
        .create(vertx)
        .bridge(options, event -> {
          System.out.println(event.type());
          event.complete(true);
        });
  }
}
