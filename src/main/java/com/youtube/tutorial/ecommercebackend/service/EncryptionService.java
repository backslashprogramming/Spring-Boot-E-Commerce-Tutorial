package com.youtube.tutorial.ecommercebackend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * Service for handling encryption of passwords.
 */
@Service
public class EncryptionService {

  /** How many salt rounds should the encryption run. */
  @Value("${encryption.salt.rounds}")
  private int saltRounds;
  /** The salt built after construction. */
  private String salt;

  /**
   * Post construction method.
   */
  @PostConstruct
  public void postConstruct() {
    salt = BCrypt.gensalt(saltRounds);
  }

  /**
   * Encrypts the given password.
   * @param password The plain text password.
   * @return The encrypted password.
   */
  public String encryptPassword(String password) {
    return BCrypt.hashpw(password, salt);
  }

  /**
   * Verifies that a password is correct.
   * @param password The plain text password.
   * @param hash The encrypted password.
   * @return True if the password is correct, false otherwise.
   */
  public boolean verifyPassword(String password, String hash) {
    return BCrypt.checkpw(password, hash);
  }

}
