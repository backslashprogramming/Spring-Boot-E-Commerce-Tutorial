package com.youtube.tutorial.ecommercebackend.api.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.youtube.tutorial.ecommercebackend.api.model.RegistrationBody;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the endpoints in the AuthenticationController.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

  /** Extension for mocking email sending. */
  @RegisterExtension
  private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
      .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
      .withPerMethodLifecycle(true);
  /** The Mocked MVC. */
  @Autowired
  private MockMvc mvc;

  /**
   * Tests the register endpoint.
   * @throws Exception
   */
  @Test
  @Transactional
  public void testRegister() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RegistrationBody body = new RegistrationBody();
    body.setEmail("AuthenticationControllerTest$testRegister@junit.com");
    body.setFirstName("FirstName");
    body.setLastName("LastName");
    body.setPassword("Password123");
    // Null or blank username.
    body.setUsername(null);
    mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    body.setUsername("");
    mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    body.setUsername("AuthenticationControllerTest$testRegister");
    // Null or blank email.
    body.setEmail(null);
    mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    body.setEmail("");
    mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    body.setEmail("AuthenticationControllerTest$testRegister@junit.com");
    // Null or blank password.
    body.setPassword(null);
    mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    body.setPassword("");
    mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    body.setPassword("Password123");
    // Null or blank first name.
    body.setFirstName(null);
    mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    body.setFirstName("");
    mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    body.setFirstName("FirstName");
    // Null or blank last name.
    body.setLastName(null);
    mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    body.setLastName("");
    mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    body.setLastName("LastName");
    //TODO: Test password characters, username length & email validity.
    // Valid registration.
    mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body)))
        .andExpect(status().is(HttpStatus.OK.value()));
  }

}
