package com.advantech.srm.persistence.entity.main.auth;

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
@Table(name = "user_accounts", schema = "auth")
public class UserAccountEntity extends AuditableEntity {
    @Column(name = "email", length = 200, nullable = false)
    private String email;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "password_hash", length = 200)
    private byte[] passwordHash;

    @Column(name = "ez_row_id", length = 50)
    private String ezRowId;

    @Column(name = "account_status", length = 20, nullable = false)
    private String accountStatus;

    @Column(name = "failed_attempts", nullable = false)
    private Integer failedAttempts = 0;

    @Column(name = "last_login_time")
    private Instant lastLoginTime;

    @Column(name = "password_updated_time", nullable = false)
    private Instant passwordUpdatedTime;
}
