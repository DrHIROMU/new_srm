package com.advantech.srm.auth.service;

import java.util.Optional;

public interface SoapAuthenticationClient {
  Optional<SoapAuthenticationResponse> authenticate(String email, String password);
}
