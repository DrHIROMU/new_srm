package com.advantech.srm.auth.service.impl;

import com.advantech.srm.auth.config.SoapClientProperties;
import com.advantech.srm.auth.config.SoapClientProperties.MockUser;
import com.advantech.srm.auth.service.SoapAuthenticationClient;
import com.advantech.srm.auth.service.SoapAuthenticationResponse;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webservices.client.WebServiceTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class LegacySoapAuthenticationClient implements SoapAuthenticationClient {

  private static final Logger log = LoggerFactory.getLogger(LegacySoapAuthenticationClient.class);

  private final SoapClientProperties properties;
  private final WebServiceTemplate webServiceTemplate;

  public LegacySoapAuthenticationClient(
      SoapClientProperties properties, WebServiceTemplateBuilder webServiceTemplateBuilder) {
    this.properties = properties;
    if (properties.isEnabled() && properties.getEndpointUri() != null) {
      this.webServiceTemplate = webServiceTemplateBuilder.setDefaultUri(properties.getEndpointUri()).build();
    } else {
      this.webServiceTemplate = null;
    }
  }

  @Override
  public Optional<SoapAuthenticationResponse> authenticate(String email, String password) {
    Optional<SoapAuthenticationResponse> mockResult = authenticateWithMockUsers(email, password);
    if (mockResult.isPresent()) {
      return mockResult;
    }

    if (!properties.isEnabled()) {
      log.warn("SOAP 認證未啟用，拒絕內部帳號登入：{}", email);
      return Optional.empty();
    }

    if (webServiceTemplate == null) {
      log.error("SOAP 認證啟用但未設定 endpointUri");
      return Optional.empty();
    }

    // TODO: 依據實際 SOAP 規格完成封包與回應解析
    throw new UnsupportedOperationException("SOAP 認證尚未實作，請配置 mockUsers 或實作 client");
  }

  private Optional<SoapAuthenticationResponse> authenticateWithMockUsers(String email, String password) {
    return properties.getMockUsers().stream()
        .filter(
            mock ->
                mock.getEmail() != null
                    && mock.getPassword() != null
                    && mock.getEmail().equalsIgnoreCase(email)
                    && mock.getPassword().equals(password))
        .findFirst()
        .map(this::toResponse);
  }

  private SoapAuthenticationResponse toResponse(MockUser mockUser) {
    return new SoapAuthenticationResponse(
        mockUser.getUserId(),
        mockUser.getEmail(),
        mockUser.getDisplayName(),
        mockUser.getRoles(),
        mockUser.getPermissions());
  }
}
