package com.advantech.srm.auth.controller;

import com.advantech.srm.auth.security.SrmUserPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserInfoController {

  @GetMapping("/userinfo")
  public ResponseEntity<UserInfoResponse> userInfo(@AuthenticationPrincipal SrmUserPrincipal principal) {
    UserInfoResponse response =
        new UserInfoResponse(
            principal.getId(),
            principal.getEmail(),
            principal.getDisplayName(),
            principal.isSupplier(),
            principal.getRoleCodes(),
            principal.getPermissions());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request) throws ServletException {
    request.logout();
    SecurityContextHolder.clearContext();
    return ResponseEntity.noContent().build();
  }

  public record UserInfoResponse(
      String id,
      String email,
      String displayName,
      boolean supplier,
      java.util.Set<String> roles,
      java.util.Set<String> permissions) {}
}
