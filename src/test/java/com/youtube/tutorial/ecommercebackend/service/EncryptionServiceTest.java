package com.youtube.tutorial.ecommercebackend.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Class to test the Encryption Service.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class EncryptionServiceTest {

  /** The EncryptionService to test. */
  @Autowired
  private EncryptionService encryptionService;

  /**
   * Tests when a password is encrypted it is still valid when verifying.
   */
  @Test
  public void testPasswordEncryption() {
    String password = "PasswordIsASecret!123";
    String hash = encryptionService.encryptPassword(password);
    Assertions.assertTrue(encryptionService.verifyPassword(password, hash), "Hashed password should match original.");
    Assertions.assertFalse(encryptionService.verifyPassword(password + "Sike!", hash), "Altered password should not be valid.");
  }

}
