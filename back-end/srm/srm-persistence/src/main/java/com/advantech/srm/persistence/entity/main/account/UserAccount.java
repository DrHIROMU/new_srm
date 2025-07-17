package com.advantech.srm.persistence.entity.main.account;

import com.advantech.srm.persistence.entity.main.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_accounts", schema = "account")
public class UserAccount extends AuditableEntity {
    @Column(length = 100)
    private String email;

    @Column(name = "password_hash", length = 256, nullable = false)
    private byte[] passwordHash;

    @Column(length = 50)
    private String name;

    @Column(name = "account_type", length = 20)
    private String accountType;

    @Column(length = 20)
    private String status;

    @Column(name = "vendor_code", length = 10)
    private String vendorCode;

    @Column(name = "ez_id", length = 36, nullable = false)
    private String ezId;

    @Column(name = "login_fail_count", nullable = false)
    private int loginFailCount = 0;

    @Column(name = "last_failed_login_time")
    private Instant lastFailedLoginTime;

    @Column(name = "last_password_changed_time")
    private Instant lastPasswordChangedTime;

    @Column(name = "last_login_time")
    private Instant lastLoginTime;

    @Column(name = "last_logout_time")
    private Instant lastLogoutTime;
}
