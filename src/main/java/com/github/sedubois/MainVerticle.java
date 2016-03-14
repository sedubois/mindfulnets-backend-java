package com.github.sedubois;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    int backendPort = parseInt(ofNullable(getenv("PORT")).orElse("3001"));
    App app = com.github.sedubois.DaggerApp.create();
    Router router = getRouter(app.practiceController().getRouter());
    vertx.createHttpServer().requestHandler(router::accept).listen(backendPort);
  }

  private Router getRouter(Router... subRouters) {
    String websiteUrl = ofNullable(getenv("WEBSITE_URL")).orElse("http://localhost:3002");
    Router router = Router.router(vertx);
    router.route("/api*").handler(CorsHandler.create(websiteUrl)
        .allowedMethod(HttpMethod.PUT));
    stream(subRouters).forEach(r -> router.mountSubRouter("/api", r));
    router.route("/eventbus/*").handler(eventBusHandler());
    router.route().failureHandler(ErrorHandler.create(true));
    router.route().handler(StaticHandler.create().setCachingEnabled(false));
    return router;
  }

  private SockJSHandler eventBusHandler() {
    BridgeOptions options = new BridgeOptions()
        .addOutboundPermitted(new PermittedOptions().setAddressRegex("timer..*"));
    return SockJSHandler
        .create(vertx)
        .bridge(options, event -> {
          if (event.type() == BridgeEventType.SOCKET_CREATED) {
            System.out.println("A socket was created");
          } else if (event.type() == BridgeEventType.SOCKET_CLOSED) {
            System.out.println("A socket was closed");
          }
          event.complete(true);
        });
  }
}
