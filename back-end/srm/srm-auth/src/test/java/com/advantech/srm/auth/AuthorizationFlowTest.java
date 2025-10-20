package com.advantech.srm.auth;

import com.advantech.srm.auth.security.SrmUserPrincipal;
import com.advantech.srm.persistence.entity.main.auth.UserAccountEntity;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationFlowTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void authorizeRequestWithAuthenticatedPrincipal() throws Exception {
    UserAccountEntity account = new UserAccountEntity();
    account.setId(1L);
    account.setEmail("supplier@example.com");
    account.setName("Supplier");
    account.setFailedAttempts(0);
    account.setPasswordUpdatedTime(Instant.now());

    SrmUserPrincipal principal = SrmUserPrincipal.fromSupplier(account);
    TestingAuthenticationToken authentication =
        new TestingAuthenticationToken(principal, "password", principal.getAuthorities());
    authentication.setAuthenticated(true);

    mockMvc
        .perform(
            get("/oauth2/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", "app-bff")
                .queryParam("scope", "openid profile")
                .queryParam("state", "dummy-state")
                .queryParam("redirect_uri", "http://localhost:8081/oauth/callback")
                .queryParam("nonce", "dummy-nonce")
                .queryParam("code_challenge", "dummy-challenge")
                .queryParam("code_challenge_method", "S256")
                .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
        .andDo(print())
        .andExpect(status().is3xxRedirection());
  }

}
