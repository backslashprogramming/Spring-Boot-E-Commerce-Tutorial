package com.youtube.tutorial.ecommercebackend.service;

import com.youtube.tutorial.ecommercebackend.api.model.LoginBody;
import com.youtube.tutorial.ecommercebackend.api.model.PasswordResetBody;
import com.youtube.tutorial.ecommercebackend.api.model.RegistrationBody;
import com.youtube.tutorial.ecommercebackend.exception.EmailFailureException;
import com.youtube.tutorial.ecommercebackend.exception.EmailNotFoundException;
import com.youtube.tutorial.ecommercebackend.exception.UserAlreadyExistsException;
import com.youtube.tutorial.ecommercebackend.exception.UserNotVerifiedException;
import com.youtube.tutorial.ecommercebackend.model.LocalUser;
import com.youtube.tutorial.ecommercebackend.model.VerificationToken;
import com.youtube.tutorial.ecommercebackend.model.dao.LocalUserDAO;
import com.youtube.tutorial.ecommercebackend.model.dao.VerificationTokenDAO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Service for handling user actions.
 */
@Service
public class UserService {

  /** The LocalUserDAO. */
  private LocalUserDAO localUserDAO;
  /** The VerificationTokenDAO. */
  private VerificationTokenDAO verificationTokenDAO;
  /** The encryption service. */
  private EncryptionService encryptionService;
  /** The JWT service. */
  private JWTService jwtService;
  /** The email service. */
  private EmailService emailService;

  /**
   * Constructor injected by spring.
   *
   * @param localUserDAO
   * @param verificationTokenDAO
   * @param encryptionService
   * @param jwtService
   * @param emailService
   */
  public UserService(LocalUserDAO localUserDAO, VerificationTokenDAO verificationTokenDAO, EncryptionService encryptionService,
                     JWTService jwtService, EmailService emailService) {
    this.localUserDAO = localUserDAO;
    this.verificationTokenDAO = verificationTokenDAO;
    this.encryptionService = encryptionService;
    this.jwtService = jwtService;
    this.emailService = emailService;
  }

  /**
   * Attempts to register a user given the information provided.
   * @param registrationBody The registration information.
   * @return The local user that has been written to the database.
   * @throws UserAlreadyExistsException Thrown if there is already a user with the given information.
   */
  public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {
    if (localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
        || localUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
      throw new UserAlreadyExistsException();
    }
    LocalUser user = new LocalUser();
    user.setEmail(registrationBody.getEmail());
    user.setUsername(registrationBody.getUsername());
    user.setFirstName(registrationBody.getFirstName());
    user.setLastName(registrationBody.getLastName());
    user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
    VerificationToken verificationToken = createVerificationToken(user);
    emailService.sendVerificationEmail(verificationToken);
    return localUserDAO.save(user);
  }

  /**
   * Creates a VerificationToken object for sending to the user.
   * @param user The user the token is being generated for.
   * @return The object created.
   */
  private VerificationToken createVerificationToken(LocalUser user) {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(jwtService.generateVerificationJWT(user));
    verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
    verificationToken.setUser(user);
    user.getVerificationTokens().add(verificationToken);
    return verificationToken;
  }

  /**
   * Logins in a user and provides an authentication token back.
   * @param loginBody The login request.
   * @return The authentication token. Null if the request was invalid.
   */
  public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
    Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(loginBody.getUsername());
    if (opUser.isPresent()) {
      LocalUser user = opUser.get();
      if (encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) {
        if (user.isEmailVerified()) {
          return jwtService.generateJWT(user);
        } else {
          List<VerificationToken> verificationTokens = user.getVerificationTokens();
          boolean resend = verificationTokens.size() == 0 ||
              verificationTokens.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));
          if (resend) {
            VerificationToken verificationToken = createVerificationToken(user);
            verificationTokenDAO.save(verificationToken);
            emailService.sendVerificationEmail(verificationToken);
          }
          throw new UserNotVerifiedException(resend);
        }
      }
    }
    return null;
  }

  /**
   * Verifies a user from the given token.
   * @param token The token to use to verify a user.
   * @return True if it was verified, false if already verified or token invalid.
   */
  @Transactional
  public boolean verifyUser(String token) {
    Optional<VerificationToken> opToken = verificationTokenDAO.findByToken(token);
    if (opToken.isPresent()) {
      VerificationToken verificationToken = opToken.get();
      LocalUser user = verificationToken.getUser();
      if (!user.isEmailVerified()) {
        user.setEmailVerified(true);
        localUserDAO.save(user);
        verificationTokenDAO.deleteByUser(user);
        return true;
      }
    }
    return false;
  }

  /**
   * Sends the user a forgot password reset based on the email provided.
   * @param email The email to send to.
   * @throws EmailNotFoundException Thrown if there is no user with that email.
   * @throws EmailFailureException
   */
  public void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException {
    Optional<LocalUser> opUser = localUserDAO.findByEmailIgnoreCase(email);
    if (opUser.isPresent()) {
      LocalUser user = opUser.get();
      String token = jwtService.generatePasswordResetJWT(user);
      emailService.sendPasswordResetEmail(user, token);
    } else {
      throw new EmailNotFoundException();
    }
  }

  /**
   * Resets the users password using a given token and email.
   * @param body The password reset information.
   */
  public void resetPassword(PasswordResetBody body) {
    String email = jwtService.getResetPasswordEmail(body.getToken());
    Optional<LocalUser> opUser = localUserDAO.findByEmailIgnoreCase(email);
    if (opUser.isPresent()) {
      LocalUser user = opUser.get();
      user.setPassword(encryptionService.encryptPassword(body.getPassword()));
      localUserDAO.save(user);
    }
  }

  /**
   * Method to check if an authenticated user has permission to a user ID.
   * @param user The authenticated user.
   * @param id The user ID.
   * @return True if they have permission, false otherwise.
   */
  public boolean userHasPermissionToUser(LocalUser user, Long id) {
    return user.getId() == id;
  }

}
