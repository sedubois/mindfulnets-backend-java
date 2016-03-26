package com.github.sedubois.user;

import java.util.Objects;

public class User {

  private Long id;

  public User(long id) {
    this.id = id;
  }

  private User() {}

  public Long getId() {
    return this.id;
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}