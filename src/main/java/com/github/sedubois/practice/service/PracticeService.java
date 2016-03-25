package com.github.sedubois.practice.service;

import com.github.sedubois.practice.Practice;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.vertx.core.Vertx;

@Singleton
public class PracticeService {

  private Practice practice;

  @Inject
  PracticeService() {
  }

  public Practice update(Practice practice) {
    Vertx vertx = Vertx.currentContext().owner();

    if (this.practice == null) {
      this.practice = new Practice(practice.getTotalSeconds());
    }

    if (this.practice.getTimerId() != null) {
      vertx.cancelTimer(this.practice.getTimerId());
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
    Vertx vertx = Vertx.currentContext().owner();

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
    System.out.println("Publishing app.timer.totalSeconds=" + totalSeconds + " to eventBus");
    Vertx.currentContext().owner().eventBus().publish("app.timer.totalSeconds", totalSeconds);
  }

  private void setRemainingSeconds(long remainingSeconds) {
    this.practice.setRemainingSeconds(remainingSeconds);
    System.out.println("Publishing app.timer.remainingSeconds=" + remainingSeconds + " to eventBus");
    Vertx.currentContext().owner().eventBus().publish("app.timer.remainingSeconds", remainingSeconds);
  }

  private void setStarted(boolean started) {
    this.practice.setStarted(started);
    System.out.println("Publishing app.timer.started=" + started + " to eventBus");
    Vertx.currentContext().owner().eventBus().publish("app.timer.started", started);
  }
}
