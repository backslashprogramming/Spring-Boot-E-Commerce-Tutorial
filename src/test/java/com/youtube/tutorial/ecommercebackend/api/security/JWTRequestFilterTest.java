package com.youtube.tutorial.ecommercebackend.api.security;

import com.youtube.tutorial.ecommercebackend.model.LocalUser;
import com.youtube.tutorial.ecommercebackend.model.dao.LocalUserDAO;
import com.youtube.tutorial.ecommercebackend.service.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class for testing the JWTRequestFilter.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class JWTRequestFilterTest {

  /** Mocked MVC. */
  @Autowired
  private MockMvc mvc;
  /** The JWT Service. */
  @Autowired
  private JWTService jwtService;
  /** The Local User DAO. */
  @Autowired
  private LocalUserDAO localUserDAO;
  /** The path that should only allow authenticated users. */
  private static final String AUTHENTICATED_PATH = "/auth/me";

  /**
   * Tests that unauthenticated requests are rejected.
   * @throws Exception
   */
  @Test
  public void testUnauthenticatedRequest() throws Exception {
    mvc.perform(get(AUTHENTICATED_PATH)).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
  }

  /**
   * Tests that bad tokens are rejected.
   * @throws Exception
   */
  @Test
  public void testBadToken() throws Exception {
    mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "BadTokenThatIsNotValid"))
        .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer BadTokenThatIsNotValid"))
        .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
  }

  /**
   * Tests unverified users who somehow get a jwt are rejected.
   * @throws Exception
   */
  @Test
  public void testUnverifiedUser() throws Exception {
    LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserB").get();
    String token = jwtService.generateJWT(user);
    mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer " + token))
        .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
  }

  /**
   * Tests the successful authentication.
   * @throws Exception
   */
  @Test
  public void testSuccessful() throws Exception {
    LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
    String token = jwtService.generateJWT(user);
    mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer " + token))
        .andExpect(status().is(HttpStatus.OK.value()));
  }

}
