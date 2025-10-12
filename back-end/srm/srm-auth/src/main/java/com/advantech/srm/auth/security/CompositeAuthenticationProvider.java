package com.advantech.srm.auth.security;

import com.advantech.srm.auth.service.SoapAuthenticationClient;
import com.advantech.srm.auth.service.SoapAuthenticationResponse;
import com.advantech.srm.auth.service.SupplierAccountService;
import com.advantech.srm.persistence.entity.main.auth.UserAccountEntity;
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

  private final SupplierAccountService supplierAccountService;
  private final SoapAuthenticationClient soapAuthenticationClient;
  private final PasswordEncoder passwordEncoder;

  public CompositeAuthenticationProvider(
      SupplierAccountService supplierAccountService,
      SoapAuthenticationClient soapAuthenticationClient,
      PasswordEncoder passwordEncoder) {
    this.supplierAccountService = supplierAccountService;
    this.soapAuthenticationClient = soapAuthenticationClient;
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
    return handleSupplierAuthentication(username, password);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

  private Authentication handleSupplierAuthentication(String email, String password) {
    Optional<UserAccountEntity> optionalAccount = supplierAccountService.findActiveByEmail(email);
    UserAccountEntity account =
        optionalAccount.orElseThrow(() -> new BadCredentialsException("供應商帳號或密碼錯誤"));

    String passwordHash = account.getPasswordHash() != null
        ? new String(account.getPasswordHash(), StandardCharsets.UTF_8)
        : null;

    if (!StringUtils.hasText(passwordHash) || !passwordEncoder.matches(password, passwordHash)) {
      throw new BadCredentialsException("供應商帳號或密碼錯誤");
    }

    SrmUserPrincipal principal = SrmUserPrincipal.fromSupplier(account);
    return UsernamePasswordAuthenticationToken.authenticated(
        principal, null, principal.getAuthorities());
  }

  private Authentication handleInternalAuthentication(String email, String password) {
    Optional<SoapAuthenticationResponse> response = soapAuthenticationClient.authenticate(email, password);
    SoapAuthenticationResponse soapAuthenticationResponse =
        response.orElseThrow(() -> new BadCredentialsException("無法驗證內部帳號"));

    SrmUserPrincipal principal = SrmUserPrincipal.fromEmployee(soapAuthenticationResponse);
    return UsernamePasswordAuthenticationToken.authenticated(
        principal, null, principal.getAuthorities());
  }

  private String resolveUsername(UsernamePasswordAuthenticationToken token) {
    Object principal = token.getPrincipal();
    if (principal instanceof String username && StringUtils.hasText(username)) {
      return username;
    }
    throw new BadCredentialsException("未提供有效的帳號");
  }

  private String resolvePassword(UsernamePasswordAuthenticationToken token) {
    Object credentials = token.getCredentials();
    if (credentials instanceof String password && StringUtils.hasText(password)) {
      return password;
    }
    throw new BadCredentialsException("未提供有效的密碼");
  }
}
