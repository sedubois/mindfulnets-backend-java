package com.github.sedubois.practice.service;

import com.github.sedubois.practice.Practice;
import io.vertx.core.Vertx;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.vertx.core.Vertx.currentContext;

@Singleton
public class PracticeService {

  private Practice practice;

  @Inject
  PracticeService() {
    this.practice = new Practice(6);
  }

  public Practice get() {
    return this.practice;
  }

  public Practice update(Practice practice) {

    if (this.practice == null) {
      this.practice = new Practice(practice.getTotalSeconds());
    }

    if (this.practice.getTimerId() != null) {
      currentContext().owner().cancelTimer(this.practice.getTimerId());
    }

    if (practice.getTotalSeconds() != null) {
      setTotalSeconds(practice.getTotalSeconds());
    }

    if (practice.getRemainingSeconds() != null) {
      setRemainingSeconds(practice.getRemainingSeconds());
    }

    if (practice.isStarted() != null) {
      setStarted(practice.isStarted());
    }

    doPractice();
    return practice;
  }

  private void doPractice() {
    Vertx vertx = currentContext().owner();

    if (this.practice.isStarted()) {
      this.practice.setTimerId(vertx.setPeriodic(1000, id -> {
        setRemainingSeconds(this.practice.getRemainingSeconds() - 1);

        if(practice.getRemainingSeconds() == 0) {
          vertx.cancelTimer(this.practice.getTimerId());
          setStarted(false);
          this.practice = null;
        }
      }));
    }
  }

  private void setTotalSeconds(long totalSeconds) {
    this.practice.setTotalSeconds(totalSeconds);
    System.out.println("Publishing app.practice.totalSeconds=" + totalSeconds + " to eventBus");
    currentContext().owner().eventBus().publish("app.practice.totalSeconds", totalSeconds);
  }

  private void setRemainingSeconds(long remainingSeconds) {
    this.practice.setRemainingSeconds(remainingSeconds);
    System.out.println("Publishing app.practice.remainingSeconds=" + remainingSeconds + " to eventBus");
    currentContext().owner().eventBus().publish("app.practice.remainingSeconds", remainingSeconds);
  }

  private void setStarted(boolean started) {
    this.practice.setStarted(started);
    System.out.println("Publishing app.practice.started=" + started + " to eventBus");
    currentContext().owner().eventBus().publish("app.practice.started", started);
  }
}
