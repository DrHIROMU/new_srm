package com.advantech.srm.persistence.entity.main;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AuditableEntity extends BaseEntity {
    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "update_time")
    private Instant updateTime;
}
