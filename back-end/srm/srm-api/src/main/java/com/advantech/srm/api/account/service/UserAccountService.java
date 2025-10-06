package com.advantech.srm.api.account.service;

import com.advantech.srm.api.account.dto.UserRegistrationRequest;
import com.advantech.srm.domain.enums.UserAccountStatusEnum;
import com.advantech.srm.persistence.entity.main.auth.UserAccountEntity;
import com.advantech.srm.persistence.repository.main.account.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class UserAccountService {
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserAccountEntity findByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }

    public UserAccountEntity registerNewUser(UserRegistrationRequest request) {
        if (userAccountRepository.findByEmail(request.getEmail()) != null) {
            throw new IllegalStateException("Email already exists");
        }

        UserAccountEntity newUser = new UserAccountEntity();
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()).getBytes());
        newUser.setName(request.getName());
        newUser.setAccountStatus(UserAccountStatusEnum.ACTIVE);
        newUser.setFailedAttempts(0);
        newUser.setPasswordUpdatedTime(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        return userAccountRepository.save(newUser);
    }
}
