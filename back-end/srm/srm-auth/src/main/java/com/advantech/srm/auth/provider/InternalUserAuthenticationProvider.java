package com.advantech.srm.auth.provider;

import com.advantech.srm.persistence.entity.main.auth.UserAccountEntity;
import com.advantech.srm.persistence.repository.main.account.UserAccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InternalUserAuthenticationProvider implements AuthenticationProvider {

    private final UserAccountRepository userAccountRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String SOAP_URL = "http://employeezone.advantech.com.tw/netLeaveRequest/webservice/EZ_SSO.asmx";
    private static final String SITENAME = "SRM"; // Or any other identifier

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        String ipAddress = getClientIp();

        boolean soapAuthSuccess = authenticateWithSoap(email, password, ipAddress);

        if (soapAuthSuccess) {
            UserAccountEntity user = userAccountRepository.findByEmail(email);
            if (user != null) {
                // You can customize authorities based on user roles/permissions
                return new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null, // Erase credentials after successful authentication
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
            } else {
                // Handle case where user is authenticated by SOAP but not in local DB
                // This could be a registration flow or an error, depending on requirements
                throw new BadCredentialsException("User not found in local database.");
            }
        } else {
            throw new BadCredentialsException("Invalid credentials provided.");
        }
    }

    private boolean authenticateWithSoap(String email, String password, String ip) {
        String soapRequest = String.format("""
                <?xml version="1.0" encoding="utf-8"?>
                <soap12:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
                  <soap12:Body>
                    <GetEZTempID xmlns="http://tempuri.org/">
                      <email>%s</email>
                      <password>%s</password>
                      <sitename>%s</sitename>
                      <ip>%s</ip>
                    </GetEZTempID>
                  </soap12:Body>
                </soap12:Envelope>
                """, email, password, SITENAME, ip);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/soap+xml; charset=utf-8"));

        HttpEntity<String> entity = new HttpEntity<>(soapRequest, headers);

        try {
            String response = restTemplate.postForObject(SOAP_URL, entity, String.class);
            // Basic check: if the response contains the result tag and it's not empty.
            // A more robust solution would parse the XML and check the value properly.
            return response != null && response.contains("<GetEZTempIDResult>") && !response.contains("<GetEZTempIDResult></GetEZTempIDResult>");
        } catch (Exception e) {
            // Log the exception in a real application
            System.err.println("SOAP authentication failed: " + e.getMessage());
            return false;
        }
    }

    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            return ip;
        }
        return "127.0.0.1"; // Fallback
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}