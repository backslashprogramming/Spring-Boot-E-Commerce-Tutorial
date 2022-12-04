package com.youtube.tutorial.ecommercebackend.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
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
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Test class to unit test the UserService class.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

  /** Extension for mocking email sending. */
  @RegisterExtension
  private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
      .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
      .withPerMethodLifecycle(true);
  /** The UserService to test. */
  @Autowired
  private UserService userService;
  /** The JWT Service. */
  @Autowired
  private JWTService jwtService;
  /** The Local User DAO. */
  @Autowired
  private LocalUserDAO localUserDAO;
  /** The encryption Service. */
  @Autowired
  private EncryptionService encryptionService;
  /** The Verification Token DAO. */
  @Autowired
  private VerificationTokenDAO verificationTokenDAO;

  /**
   * Tests the registration process of the user.
   * @throws MessagingException Thrown if the mocked email service fails somehow.
   */
  @Test
  @Transactional
  public void testRegisterUser() throws MessagingException {
    RegistrationBody body = new RegistrationBody();
    body.setUsername("UserA");
    body.setEmail("UserServiceTest$testRegisterUser@junit.com");
    body.setFirstName("FirstName");
    body.setLastName("LastName");
    body.setPassword("MySecretPassword123");
    Assertions.assertThrows(UserAlreadyExistsException.class,
        () -> userService.registerUser(body), "Username should already be in use.");
    body.setUsername("UserServiceTest$testRegisterUser");
    body.setEmail("UserA@junit.com");
    Assertions.assertThrows(UserAlreadyExistsException.class,
        () -> userService.registerUser(body), "Email should already be in use.");
    body.setEmail("UserServiceTest$testRegisterUser@junit.com");
    Assertions.assertDoesNotThrow(() -> userService.registerUser(body),
        "User should register successfully.");
    Assertions.assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0]
        .getRecipients(Message.RecipientType.TO)[0].toString());
  }

  /**
   * Tests the loginUser method.
   * @throws UserNotVerifiedException
   * @throws EmailFailureException
   */
  @Test
  @Transactional
  public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
    LoginBody body = new LoginBody();
    body.setUsername("UserA-NotExists");
    body.setPassword("PasswordA123-BadPassword");
    Assertions.assertNull(userService.loginUser(body), "The user should not exist.");
    body.setUsername("UserA");
    Assertions.assertNull(userService.loginUser(body), "The password should be incorrect.");
    body.setPassword("PasswordA123");
    Assertions.assertNotNull(userService.loginUser(body), "The user should login successfully.");
    body.setUsername("UserB");
    body.setPassword("PasswordB123");
    try {
      userService.loginUser(body);
      Assertions.assertTrue(false, "User should not have email verified.");
    } catch (UserNotVerifiedException ex) {
      Assertions.assertTrue(ex.isNewEmailSent(), "Email verification should be sent.");
      Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
    }
    try {
      userService.loginUser(body);
      Assertions.assertTrue(false, "User should not have email verified.");
    } catch (UserNotVerifiedException ex) {
      Assertions.assertFalse(ex.isNewEmailSent(), "Email verification should not be resent.");
      Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
    }
  }

  /**
   * Tests the verifyUser method.
   * @throws EmailFailureException
   */
  @Test
  @Transactional
  public void testVerifyUser() throws EmailFailureException {
    Assertions.assertFalse(userService.verifyUser("Bad Token"), "Token that is bad or does not exist should return false.");
    LoginBody body = new LoginBody();
    body.setUsername("UserB");
    body.setPassword("PasswordB123");
    try {
      userService.loginUser(body);
      Assertions.assertTrue(false, "User should not have email verified.");
    } catch (UserNotVerifiedException ex) {
      List<VerificationToken> tokens = verificationTokenDAO.findByUser_IdOrderByIdDesc(2L);
      String token = tokens.get(0).getToken();
      Assertions.assertTrue(userService.verifyUser(token), "Token should be valid.");
      Assertions.assertNotNull(body, "The user should now be verified.");
    }
  }

  /**
   * Tests the forgotPassword method in the User Service.
   * @throws MessagingException
   */
  @Test
  @Transactional
  public void testForgotPassword() throws MessagingException {
    Assertions.assertThrows(EmailNotFoundException.class,
        () -> userService.forgotPassword("UserNotExist@junit.com"));
    Assertions.assertDoesNotThrow(() -> userService.forgotPassword(
        "UserA@junit.com"), "Non existing email should be rejected.");
    Assertions.assertEquals("UserA@junit.com",
        greenMailExtension.getReceivedMessages()[0]
        .getRecipients(Message.RecipientType.TO)[0].toString(), "Password " +
            "reset email should be sent.");
  }

  /**
   * Tests the resetPassword method in the User Service.
   * @throws MessagingException
   */
  public void testResetPassword() {
    LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
    String token = jwtService.generatePasswordResetJWT(user);
    PasswordResetBody body = new PasswordResetBody();
    body.setToken(token);
    body.setPassword("Password123456");
    userService.resetPassword(body);
    user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
    Assertions.assertTrue(encryptionService.verifyPassword("Password123456",
        user.getPassword()), "Password change should be written to DB.");
  }

}
