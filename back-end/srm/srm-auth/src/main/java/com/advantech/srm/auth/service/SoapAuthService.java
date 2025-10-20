package com.advantech.srm.auth.service;

import com.advantech.srm.auth.security.SrmUserPrincipal;
import java.util.Optional;

public interface SoapAuthService {

  Optional<SrmUserPrincipal> authenticate(String email, String password);
}
