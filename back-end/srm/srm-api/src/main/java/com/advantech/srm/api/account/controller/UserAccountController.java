package com.advantech.srm.api.account.controller;

import com.advantech.srm.api.account.service.UserAccountService;
import com.advantech.srm.persistence.entity.main.account.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class UserAccountController {
    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/user-accounts/{email}")
    public UserAccount findByEmail(@PathVariable String email) {
        return userAccountService.findByEmail(email);
    }
}
