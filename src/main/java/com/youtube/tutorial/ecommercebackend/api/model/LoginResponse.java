package com.youtube.tutorial.ecommercebackend.api.model;

/**
 * The response object sent from login request.
 */
public class LoginResponse {

  /** The JWT token to be used for authentication. */
  private String jwt;

  public String getJwt() {
    return jwt;
  }

  public void setJwt(String jwt) {
    this.jwt = jwt;
  }
}
