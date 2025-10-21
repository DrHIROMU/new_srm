package com.advantech.srm.persistence.entity.main.rfq;

import com.advantech.srm.persistence.entity.main.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Entity
@Table(name = "material_category", schema = "rfq")
public class MaterialCategoryEntity extends BaseEntity {

    @Column(name = "purchasing_category_id", nullable = false)
    private Long purchasingCategoryId;
}
