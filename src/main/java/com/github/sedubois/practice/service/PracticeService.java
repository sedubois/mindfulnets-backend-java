package com.github.sedubois.practice.service;

import com.github.sedubois.practice.Practice;

import java.util.Collection;

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
        this.practice.setRemainingSeconds(this.practice.getRemainingSeconds() - 1);
        vertx.eventBus().publish("timer.remainingSeconds", practice.getRemainingSeconds());
      
        if(practice.getRemainingSeconds() == 0) {
          vertx.cancelTimer(this.practice.getTimerId());
          vertx.eventBus().publish("timer.started", false);
          this.practice = null;
        }
      }));
    }
  }
  
  private void setTotalSeconds(long totalSeconds) {
    this.practice.setTotalSeconds(totalSeconds);
    Vertx.currentContext().owner().eventBus().publish("timer.totalSeconds", totalSeconds);
  }
  
  private void setRemainingSeconds(long remainingSeconds) {
    this.practice.setRemainingSeconds(remainingSeconds);
    Vertx.currentContext().owner().eventBus().publish("timer.remainingSeconds", remainingSeconds);
  }
  
  private void setStarted(boolean started) {
    this.practice.setStarted(started);
    Vertx.currentContext().owner().eventBus().publish("timer.started", started);
  }
}
