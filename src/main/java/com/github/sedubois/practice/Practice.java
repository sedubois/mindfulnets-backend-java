package com.github.sedubois.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;

public class Practice {

  private Long totalSeconds;
  private Long remainingSeconds;
  private Boolean started;
  private transient Long timerId;

  public Practice(long totalSeconds) {
    this.totalSeconds = totalSeconds;
    this.remainingSeconds = totalSeconds;
    this.started = false;
  }

  private Practice() {}

  public Long getTotalSeconds() {
    return totalSeconds;
  }

  public void setTotalSeconds(long totalSeconds) {
    this.totalSeconds = totalSeconds;
  }
  
  public Long getRemainingSeconds() {
    return remainingSeconds;
  }
  
  public void setRemainingSeconds(long remainingSeconds) {
    this.remainingSeconds = remainingSeconds;
  }
  
  public Long getTimerId() {
    return timerId;
  }
 
  public void setTimerId(long timerId) {
    this.timerId = timerId;
  }

  public Boolean isStarted() {
    return started;
  }
  
  public void setStarted(boolean started) {
    this.started = started;
  }
  
  @Override
  public String toString() {
    return String.format("[totalSeconds = %d, remainingSeconds = %d, started = %b, timerId = %d]", totalSeconds, remainingSeconds, started, timerId);
  }
}