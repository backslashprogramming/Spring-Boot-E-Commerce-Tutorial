package com.youtube.tutorial.ecommercebackend.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

/**
 * Configuration of the security on endpoints.
 */
@Configuration
public class WebSecurityConfig {

  private JWTRequestFilter jwtRequestFilter;

  public WebSecurityConfig(JWTRequestFilter jwtRequestFilter) {
    this.jwtRequestFilter = jwtRequestFilter;
  }

  /**
   * Filter chain to configure security.
   * @param http The security object.
   * @return The chain built.
   * @throws Exception Thrown on error configuring.
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable().cors().disable();
    // We need to make sure our authentication filter is run before the http request filter is run.
    http.addFilterBefore(jwtRequestFilter, AuthorizationFilter.class);
    http.authorizeHttpRequests()
        // Specific exclusions or rules.
        .requestMatchers("/product", "/auth/register", "/auth/login",
            "/auth/verify", "/auth/forgot", "/auth/reset", "/error",
            "/websocket", "/websocket/**").permitAll()
        // Everything else should be authenticated.
        .anyRequest().authenticated();
    return http.build();
  }

}
