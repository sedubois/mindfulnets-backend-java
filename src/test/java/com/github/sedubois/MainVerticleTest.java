package com.github.sedubois;

import com.github.sedubois.practice.Practice;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

// TODO use Spock / assertJ / rest assured to get given/when/then style
@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

  private static final int PORT = 3001;
  private static final String ADDRESS = "localhost";
  private Vertx vertx = Vertx.vertx();
  private HttpClient httpClient;

  private static final DeploymentOptions DEPLOYMENT_OPTIONS = new DeploymentOptions()
      .setConfig(new JsonObject()
          .put("server.port", PORT)
          .put("client.url", "http://localhost:3002"));

  @Before
  public void setUp(TestContext context) {
    vertx.deployVerticle(MainVerticle.class.getName(), DEPLOYMENT_OPTIONS, context.asyncAssertSuccess());
    httpClient = vertx.createHttpClient();
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void createPracticeReturnsCreatedPractice(TestContext context) {
    final Async async = context.async();

    long totalSeconds = 10, remainingSeconds = 5;
    String body = "{\"totalSeconds\": " + totalSeconds + ", \"remainingSeconds\": " + remainingSeconds + "}";
    httpClient
        .put(PORT, ADDRESS, "/api/practices", response -> response.handler(responseBody -> {
          System.out.println(responseBody);
          Practice actual = Json.decodeValue(responseBody.toString(), Practice.class);
          Practice expected = new Practice(totalSeconds);
          expected.setRemainingSeconds(remainingSeconds);
          expected.setTimerId(0);
          context.assertTrue(actual.equals(expected));
          async.complete();
        }))
        .putHeader("content-type", "application/json")
        .putHeader("content-length", Integer.toString(body.length()))
        .write(body)
        .end();
  }
}
