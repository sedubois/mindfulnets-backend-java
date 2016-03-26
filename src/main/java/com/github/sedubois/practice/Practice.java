package com.github.sedubois.practice;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Practice practice = (Practice) o;
    return Objects.equals(totalSeconds, practice.totalSeconds) &&
        Objects.equals(remainingSeconds, practice.remainingSeconds) &&
        Objects.equals(started, practice.started) &&
        Objects.equals(timerId, practice.timerId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalSeconds, remainingSeconds, started, timerId);
  }
}