package com.github.sedubois;

import com.github.sedubois.practice.Practice;

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

  @Before
  public void setUp(TestContext context) {
    vertx.deployVerticle(MainVerticle.class.getName(), context.asyncAssertSuccess());
    httpClient = vertx.createHttpClient();
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void createPracticeReturnsCreatedPractice(TestContext context) {
    final Async async = context.async();

    long totalSeconds = 10;
    String body = "{\"totalSeconds\": " + totalSeconds + "}";
    httpClient
        .post(PORT, ADDRESS, "/api/practices", response -> response.handler(responseBody -> {
          System.out.println(responseBody);
          Practice actual = Json.decodeValue(responseBody.toString(), Practice.class);
          Practice expected = new Practice(totalSeconds);
          context.assertTrue(actual.equals(expected));
          async.complete();
        }))
        .putHeader("content-type", "application/json")
        .putHeader("content-length", Integer.toString(body.length()))
        .write(body)
        .end();
  }
}
