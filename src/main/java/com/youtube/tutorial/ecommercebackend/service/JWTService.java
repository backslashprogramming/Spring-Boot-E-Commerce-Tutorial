package com.youtube.tutorial.ecommercebackend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.youtube.tutorial.ecommercebackend.model.LocalUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Service for handling JWTs for user authentication.
 */
@Service
public class JWTService {

  /** The secret key to encrypt the JWTs with. */
  @Value("${jwt.algorithm.key}")
  private String algorithmKey;
  /** The issuer the JWT is signed with. */
  @Value("${jwt.issuer}")
  private String issuer;
  /** How many seconds from generation should the JWT expire? */
  @Value("${jwt.expiryInSeconds}")
  private int expiryInSeconds;
  /** The algorithm generated post construction. */
  private Algorithm algorithm;
  /** The JWT claim key for the username. */
  private static final String USERNAME_KEY = "USERNAME";
  private static final String VERIFICATION_EMAIL_KEY = "VERIFICATION_EMAIL";
  private static final String RESET_PASSWORD_EMAIL_KEY = "RESET_PASSWORD_EMAIL";

  /**
   * Post construction method.
   */
  @PostConstruct
  public void postConstruct() {
    algorithm = Algorithm.HMAC256(algorithmKey);
  }

  /**
   * Generates a JWT based on the given user.
   * @param user The user to generate for.
   * @return The JWT.
   */
  public String generateJWT(LocalUser user) {
    return JWT.create()
        .withClaim(USERNAME_KEY, user.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
        .withIssuer(issuer)
        .sign(algorithm);
  }

  /**
   * Generates a special token for verification of an email.
   * @param user The user to create the token for.
   * @return The token generated.
   */
  public String generateVerificationJWT(LocalUser user) {
    return JWT.create()
        .withClaim(VERIFICATION_EMAIL_KEY, user.getEmail())
        .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
        .withIssuer(issuer)
        .sign(algorithm);
  }

  /**
   * Generates a JWT for use when resetting a password.
   * @param user The user to generate for.
   * @return The generated JWT token.
   */
  public String generatePasswordResetJWT(LocalUser user) {
    return JWT.create()
        .withClaim(RESET_PASSWORD_EMAIL_KEY, user.getEmail())
        .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60 * 30)))
        .withIssuer(issuer)
        .sign(algorithm);
  }

  /**
   * Gets the email from a password reset token.
   * @param token The token to use.
   * @return The email in the token if valid.
   */
  public String getResetPasswordEmail(String token) {
    DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
    return jwt.getClaim(RESET_PASSWORD_EMAIL_KEY).asString();
  }

  /**
   * Gets the username out of a given JWT.
   * @param token The JWT to decode.
   * @return The username stored inside.
   */
  public String getUsername(String token) {
    DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
    return jwt.getClaim(USERNAME_KEY).asString();
  }

}
