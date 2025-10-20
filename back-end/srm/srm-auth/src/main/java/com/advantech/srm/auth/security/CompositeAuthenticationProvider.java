package com.advantech.srm.auth.security;

import com.advantech.srm.auth.service.SoapAuthService;
import com.advantech.srm.persistence.entity.main.auth.UserAccountEntity;
import com.advantech.srm.persistence.repository.main.account.UserAccountRepository;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CompositeAuthenticationProvider implements AuthenticationProvider {

  private final SoapAuthService soapAuthService;
  private final UserAccountRepository userAccountRepository;
  private final PasswordEncoder passwordEncoder;

  public CompositeAuthenticationProvider(
      SoapAuthService soapAuthService,
      UserAccountRepository userAccountRepository,
      PasswordEncoder passwordEncoder) {
    this.soapAuthService = soapAuthService;
    this.userAccountRepository = userAccountRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    if (!(authentication instanceof UsernamePasswordAuthenticationToken token)) {
      return null;
    }
    String username = resolveUsername(token);
    String password = resolvePassword(token);

    if (username.toLowerCase(Locale.ENGLISH).endsWith("@advantech.com")) {
      return handleInternalAuthentication(username, password);
    }
    return handleExternalAuthentication(username, password);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

  private Authentication handleInternalAuthentication(String email, String password) {
    Optional<SrmUserPrincipal> principal = soapAuthService.authenticate(email, password);
    SrmUserPrincipal userPrincipal =
        principal.orElseThrow(
            () -> new BadCredentialsException("Unable to authenticate against internal directory"));
    return UsernamePasswordAuthenticationToken.authenticated(
        userPrincipal, null, userPrincipal.getAuthorities());
  }

  private Authentication handleExternalAuthentication(String username, String rawPassword) {
    UserAccountEntity account = userAccountRepository.findByEmail(username);
    if (account == null) {
      throw new BadCredentialsException("Invalid username or password");
    }
    String encodedPassword = extractEncodedPassword(account);
    if (encodedPassword == null || !passwordEncoder.matches(rawPassword, encodedPassword)) {
      throw new BadCredentialsException("Invalid username or password");
    }
    SrmUserPrincipal principal = SrmUserPrincipal.fromSupplier(account);
    return UsernamePasswordAuthenticationToken.authenticated(
        principal, null, principal.getAuthorities());
  }

  private String extractEncodedPassword(UserAccountEntity account) {
    if (account.getPasswordHash() == null || account.getPasswordHash().length == 0) {
      return null;
    }
    String stored = new String(account.getPasswordHash(), StandardCharsets.UTF_8).trim();
    if (stored.isEmpty()) {
      return null;
    }
    if (stored.startsWith("{")) {
      return stored;
    }
    // Delegate encoders expect an algorithm prefix; default to bcrypt for legacy hashes.
    return "{bcrypt}" + stored;
  }

  private String resolveUsername(UsernamePasswordAuthenticationToken token) {
    Object principal = token.getPrincipal();
    if (principal instanceof String username && StringUtils.hasText(username)) {
      return username;
    }
    throw new BadCredentialsException("Username must not be blank");
  }

  private String resolvePassword(UsernamePasswordAuthenticationToken token) {
    Object credentials = token.getCredentials();
    if (credentials instanceof String password && StringUtils.hasText(password)) {
      return password;
    }
    throw new BadCredentialsException("Password must not be blank");
  }
}
