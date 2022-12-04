package com.youtube.tutorial.ecommercebackend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.youtube.tutorial.ecommercebackend.model.LocalUser;
import com.youtube.tutorial.ecommercebackend.model.dao.LocalUserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Class to test the JWTService.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class JWTServiceTest {

  /** The JWTService to test. */
  @Autowired
  private JWTService jwtService;
   /** The Local User DAO. */
  @Autowired
  private LocalUserDAO localUserDAO;
  /** The algorithm key we're using in the properties file. */
  @Value("${jwt.algorithm.key}")
  private String algorithmKey;

  /**
   * Tests that the verification token is not usable for login.
   */
  @Test
  public void testVerificationTokenNotUsableForLogin() {
    LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
    String token = jwtService.generateVerificationJWT(user);
    Assertions.assertNull(jwtService.getUsername(token), "Verification token should not contain username.");
  }

  /**
   * Tests that the authentication token generate still returns the username.
   */
  @Test
  public void testAuthTokenReturnsUsername() {
    LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
    String token = jwtService.generateJWT(user);
    Assertions.assertEquals(user.getUsername(), jwtService.getUsername(token), "Token for auth should contain users username.");
  }

  /**
   * Tests that when someone generates a JWT with an algorithm different to
   * ours the verification rejects the token as the signature is not verified.
   */
  @Test
  public void testLoginJWTNotGeneratedByUs() {
    String token =
        JWT.create().withClaim("USERNAME", "UserA").sign(Algorithm.HMAC256(
        "NotTheRealSecret"));
    Assertions.assertThrows(SignatureVerificationException.class,
        () -> jwtService.getUsername(token));
  }

  /**
   * Tests that when a JWT token is generated if it does not contain us as
   * the issuer we reject it.
   */
  @Test
  public void testLoginJWTCorrectlySignedNoIssuer() {
    String token =
        JWT.create().withClaim("USERNAME", "UserA")
            .sign(Algorithm.HMAC256(algorithmKey));
    Assertions.assertThrows(MissingClaimException.class,
        () -> jwtService.getUsername(token));
  }

  /**
   * Tests that when someone generates a JWT with an algorithm different to
   * ours the verification rejects the token as the signature is not verified.
   */
  @Test
  public void testResetPasswordJWTNotGeneratedByUs() {
    String token =
        JWT.create().withClaim("RESET_PASSWORD_EMAIL", "UserA@junit.com").sign(Algorithm.HMAC256(
            "NotTheRealSecret"));
    Assertions.assertThrows(SignatureVerificationException.class,
        () -> jwtService.getResetPasswordEmail(token));
  }

  /**
   * Tests that when a JWT token is generated if it does not contain us as
   * the issuer we reject it.
   */
  @Test
  public void testResetPasswordJWTCorrectlySignedNoIssuer() {
    String token =
        JWT.create().withClaim("RESET_PASSWORD_EMAIL", "UserA@junit.com")
            .sign(Algorithm.HMAC256(algorithmKey));
    Assertions.assertThrows(MissingClaimException.class,
        () -> jwtService.getResetPasswordEmail(token));
  }

  /**
   * Tests the password reset generation and verification.
   */
  @Test
  public void testPasswordResetToken() {
    LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
    String token = jwtService.generatePasswordResetJWT(user);
    Assertions.assertEquals(user.getEmail(),
        jwtService.getResetPasswordEmail(token), "Email should match inside " +
            "JWT.");
  }

}
