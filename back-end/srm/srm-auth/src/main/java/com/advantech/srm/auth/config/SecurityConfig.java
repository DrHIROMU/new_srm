package com.advantech.srm.auth.config;

import com.advantech.srm.auth.security.CompositeAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder passwordEncoder) {
    User.UserBuilder builder = User.builder().passwordEncoder(passwordEncoder::encode);
    return new InMemoryUserDetailsManager(
        builder
            .username("user@example.com")
            .password("P@ssw0rd")
            .authorities("ROLE_SUPPLIER", "vendors.read")
            .build());
  }

  @Bean
  public AuthenticationManager authenticationManager(
      HttpSecurity http, CompositeAuthenticationProvider authenticationProvider) throws Exception {
    AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
    builder.authenticationProvider(authenticationProvider);
    return builder.build();
  }
}


