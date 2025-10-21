package com.advantech.srm.persistence.repository.main.rfq;

import com.advantech.srm.persistence.entity.main.rfq.MaterialCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialCategoryRepository extends JpaRepository<MaterialCategoryEntity, Long> {
}
