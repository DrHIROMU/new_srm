package com.advantech.srm.auth.service.impl;

import com.advantech.srm.auth.security.SrmUserPrincipal;
import com.advantech.srm.auth.service.SoapAuthService;
import com.advantech.srm.auth.service.SoapAuthenticationResponse;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class StubSoapAuthService implements SoapAuthService {

  @Override
  public Optional<SrmUserPrincipal> authenticate(String email, String password) {
    if (!StringUtils.hasText(email) || !email.toLowerCase().endsWith("@advantech.com")) {
      return Optional.empty();
    }
    if (!StringUtils.hasText(password)) {
      return Optional.empty();
    }
    String displayName = email.substring(0, email.indexOf('@'));
    SoapAuthenticationResponse response =
        new SoapAuthenticationResponse(
            email,
            email,
            displayName.isBlank() ? email : displayName,
            Set.of("ROLE_INTERNAL"),
            Set.of("vendors.read"));
    return Optional.of(SrmUserPrincipal.fromEmployee(response));
  }
}
