package com.advantech.srm.persistence.entity.main.master;

import com.advantech.srm.persistence.entity.main.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "purchasing_org", schema = "master")
public class PurchasingOrgEntity extends BaseEntity {
    @Column(name = "org", nullable = false, length = 50)
    private String org;

    @Column(name = "org_description", length = 255)
    private String orgDescription;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
