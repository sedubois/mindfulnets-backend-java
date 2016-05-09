package com.github.sedubois;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import static io.vertx.ext.auth.oauth2.OAuth2FlowType.AUTH_CODE;
import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;
import static java.util.Optional.ofNullable;

public class MainVerticle extends AbstractVerticle {

  private static final int PORT = parseInt(ofNullable(getenv("PORT")).orElse("3001"));
  // cfr. http://vertx.io/docs/vertx-auth-oauth2/java/#_example_configuration_for_common_oauth2_providers
  // TODO test and use these credentials
  private final JsonObject FACEBOOK_CONFIG = new JsonObject()
      .put("clientID", "CLIENT_ID")
      .put("clientSecret", "CLIENT_SECRET")
      .put("site", "https://www.facebook.com")
      .put("authorizationPath", "/dialog/oauth")
      .put("tokenPath", "https://graph.facebook.com/oauth/access_token");
  // TODO don't store in VCS
  private final JsonObject GITHUB_CONFIG = new JsonObject()
      .put("clientID", "cdc163e5f5695e16787b")
      .put("clientSecret", "1cc8e3a615af3a8ca47cd1f15adcfcdd1e880db4")
      .put("site", "https://github.com/login")
      .put("tokenPath", "/oauth/access_token")
      .put("authorizationPath", "/oauth/authorize");
  private OAuth2Auth githubAuth;

  @Override
  public void start() throws Exception {
    githubAuth = OAuth2Auth.create(vertx, AUTH_CODE, GITHUB_CONFIG);
    vertx
        .createHttpServer()
        .requestHandler(router()::accept)
        .listen(PORT);
  }

  private Router router() {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.route().handler(LoggerHandler.create());
    router.route().failureHandler(ErrorHandler.create());
    router.route().consumes("application/json");
    router.route().produces("application/json");
    setupAuthentication(router);
    router.mountSubRouter("/api", apiRouter());
    router.route("/eventbus/*").handler(eventBusHandler());
    return router;
  }

  private void setupAuthentication(Router router) {
    // cfr. http://vertx.io/docs/vertx-web/java/#_oauth2authhandler_handler

    OAuth2AuthHandler oauth2Handler = OAuth2AuthHandler
        .create(githubAuth, "http://localhost:3001")
        .setupCallback(router.get("/callback"));

    router.route("/protected/*").handler(oauth2Handler);

    router.route("/protected/somepage")
        .handler(rc -> rc.response()
            .end("Welcome, this page is protected!"));
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
