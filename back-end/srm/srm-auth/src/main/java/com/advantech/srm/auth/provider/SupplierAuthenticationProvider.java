package com.advantech.srm.auth.provider;

import com.advantech.srm.persistence.entity.main.auth.UserAccountEntity;
import com.advantech.srm.persistence.repository.main.auth.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class SupplierAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // This provider only handles non-advantech users (suppliers)
        if (username.toLowerCase().contains("@advantech.com")) {
            return null;
        }

        UserAccountEntity user = userAccountRepository.findByEmail(username);
        if(user == null) {
            throw new BadCredentialsException("User not found");
        }
        if (passwordEncoder.matches(password, new String(user.getPasswordHash()))) {
            // For simplicity, grant a single 'SUPPLIER' role.
            return new UsernamePasswordAuthenticationToken(
                    username,
                    password,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_SUPPLIER"))
            );
        } else {
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
