package com.advantech.srm.persistence.entity.main;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AuditableEntity extends BaseEntity {
    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_time")
    private Instant updatedTime;

    @PrePersist
    public void onCreate() {
        super.onCreate();
        this.updatedTime = super.getCreatedTime();
        if (this.updatedBy == null) {
            this.updatedBy = "SYSTEM";
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedTime = Instant.now();
        if (this.updatedBy == null) {
            this.updatedBy = "SYSTEM";
        }
    }
}
