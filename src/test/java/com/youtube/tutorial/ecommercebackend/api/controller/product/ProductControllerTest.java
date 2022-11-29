package com.youtube.tutorial.ecommercebackend.api.controller.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class for testing the ProductController
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

  /** Mocked MVC. */
  @Autowired
  private MockMvc mvc;

  /**
   * Tests getting the product list.
   * @throws Exception
   */
  @Test
  public void testProductList() throws Exception {
    mvc.perform(get("/product")).andExpect(status().is(HttpStatus.OK.value()));
  }

}
