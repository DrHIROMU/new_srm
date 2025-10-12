package com.advantech.srm.auth.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.advantech.srm.auth.service.SoapAuthenticationClient;
import com.advantech.srm.auth.service.SoapAuthenticationResponse;
import com.advantech.srm.auth.service.SupplierAccountService;
import com.advantech.srm.domain.enums.UserAccountStatusEnum;
import com.advantech.srm.persistence.entity.main.auth.UserAccountEntity;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

class CompositeAuthenticationProviderTest {

  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Test
  void authenticateSupplierSuccess() {
    UserAccountEntity account = new UserAccountEntity();
    account.setId(1L);
    account.setEmail("supplier@example.com");
    account.setName("Supplier User");
    account.setPasswordHash(passwordEncoder.encode("secret").getBytes(StandardCharsets.UTF_8));
    account.setAccountStatus(UserAccountStatusEnum.ACTIVE);

    SupplierAccountService supplierService = email -> Optional.of(account);
    SoapAuthenticationClient soapClient = (email, password) -> Optional.empty();

    CompositeAuthenticationProvider provider =
        new CompositeAuthenticationProvider(supplierService, soapClient, passwordEncoder);

    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken("supplier@example.com", "secret");

    UsernamePasswordAuthenticationToken authenticated =
        (UsernamePasswordAuthenticationToken) provider.authenticate(token);

    assertThat(authenticated.isAuthenticated()).isTrue();
    assertThat(authenticated.getPrincipal()).isInstanceOf(SrmUserPrincipal.class);

    SrmUserPrincipal principal = (SrmUserPrincipal) authenticated.getPrincipal();
    assertThat(principal.isSupplier()).isTrue();
    assertThat(principal.getRoleCodes()).containsExactly("ROLE_SUPPLIER");
    assertThat(principal.getPermissions()).isEmpty();
  }

  @Test
  void authenticateEmployeeSuccess() {
    SupplierAccountService supplierService = email -> Optional.empty();
    SoapAuthenticationClient soapClient =
        (email, password) ->
            Optional.of(
                new SoapAuthenticationResponse(
                    "employee-1",
                    "user@advantech.com",
                    "Advantech User",
                    Set.of("ROLE_INTERNAL"),
                    Set.of("vendors.read")));

    CompositeAuthenticationProvider provider =
        new CompositeAuthenticationProvider(supplierService, soapClient, passwordEncoder);

    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken("user@advantech.com", "secret");

    UsernamePasswordAuthenticationToken authenticated =
        (UsernamePasswordAuthenticationToken) provider.authenticate(token);

    SrmUserPrincipal principal = (SrmUserPrincipal) authenticated.getPrincipal();
    assertThat(principal.isSupplier()).isFalse();
    assertThat(principal.getRoleCodes()).contains("ROLE_INTERNAL");
  }

  @Test
  void authenticateSupplierWithInvalidPasswordThrows() {
    UserAccountEntity account = new UserAccountEntity();
    account.setEmail("supplier@example.com");
    account.setName("Supplier User");
    account.setPasswordHash(passwordEncoder.encode("secret").getBytes(StandardCharsets.UTF_8));
    account.setAccountStatus(UserAccountStatusEnum.ACTIVE);

    SupplierAccountService supplierService = email -> Optional.of(account);
    SoapAuthenticationClient soapClient = (email, password) -> Optional.empty();

    CompositeAuthenticationProvider provider =
        new CompositeAuthenticationProvider(supplierService, soapClient, passwordEncoder);

    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken("supplier@example.com", "wrong");

    assertThrows(BadCredentialsException.class, () -> provider.authenticate(token));
  }
}
