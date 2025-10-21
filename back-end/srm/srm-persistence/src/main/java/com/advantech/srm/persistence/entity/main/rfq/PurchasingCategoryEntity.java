package com.advantech.srm.persistence.entity.main.rfq;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Entity
@Table(name = "purchaseing_category", schema = "rfq")
public class PurchasingCategoryEntity extends RFQBaseCategoryEntity {
}
