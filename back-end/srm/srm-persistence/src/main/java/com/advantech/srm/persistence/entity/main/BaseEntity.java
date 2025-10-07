package com.advantech.srm.persistence.entity.main;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "created_time", updatable = false)
    private Instant createdTime;

    @PrePersist
    public void onCreate() {
        this.createdTime = Instant.now();
        if (this.createdBy == null) {
            this.createdBy = "SYSTEM";
        }
    }
}
