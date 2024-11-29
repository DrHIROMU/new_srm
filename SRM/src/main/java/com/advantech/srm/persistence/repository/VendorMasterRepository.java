package com.advantech.srm.persistence.repository;

import com.advantech.srm.persistence.model.VendorMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorMasterRepository extends JpaRepository<VendorMasterEntity, Long> {

}
