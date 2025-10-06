package com.advantech.srm.api.account.controller;

import com.advantech.srm.api.account.dto.UserRegistrationRequest;
import com.advantech.srm.api.account.service.UserAccountService;
import com.advantech.srm.persistence.entity.main.auth.UserAccountEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
@Tag(name = "User Account", description = "使用者帳戶管理 API")
public class UserAccountController {
    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/user-accounts/{email}")
    @Operation(summary = "根據 email 查詢使用者", description = "透過 email 查詢使用者帳戶資訊")
    public UserAccountEntity findByEmail(@PathVariable String email) {
        return userAccountService.findByEmail(email);
    }

    @PostMapping("/register")
    @Operation(summary = "註冊新使用者", description = "建立新的使用者帳戶")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest registrationRequest) {
        try {
            userAccountService.registerNewUser(registrationRequest);
            return ResponseEntity.ok("User registered successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
