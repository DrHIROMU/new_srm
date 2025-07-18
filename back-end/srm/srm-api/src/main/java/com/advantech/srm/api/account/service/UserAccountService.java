package com.advantech.srm.api.account.service;

import com.advantech.srm.persistence.entity.main.account.UserAccount;
import com.advantech.srm.persistence.repository.main.account.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {
    @Autowired
    private UserAccountRepository userAccountRepository;

    public UserAccount findByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }
}
