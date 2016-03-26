package com.github.sedubois.user;

import java.util.Objects;

public class User {

  private Long id;
  private String name;

  public User(long id) {
    this.id = id;
    this.name = "user-" + id;
  }

  private User() {}

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id) &&
        Objects.equals(name, user.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }
}