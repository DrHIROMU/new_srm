package com.advantech.srm.persistence.entity.main.rfq;

import com.advantech.srm.persistence.entity.main.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RFQBaseCategoryEntity extends BaseEntity {
    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "translation_key", length = 100)
    private String translationKey;
}
