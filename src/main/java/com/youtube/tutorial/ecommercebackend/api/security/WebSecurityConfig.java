package com.youtube.tutorial.ecommercebackend.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration of the security on endpoints.
 */
@Configuration
public class WebSecurityConfig {

  /**
   * Filter chain to configure security.
   * @param http The security object.
   * @return The chain built.
   * @throws Exception Thrown on error configuring.
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //TODO: Proper authentication.
    http.csrf().disable().cors().disable();
    http.authorizeHttpRequests().anyRequest().permitAll();
    return http.build();
  }

}
