package com.advantech.srm.persistence.repository.main.master;

import com.advantech.srm.persistence.entity.main.master.PurchasingOrgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchasingOrgRepository extends JpaRepository<PurchasingOrgEntity, Long> {
    List<PurchasingOrgEntity> findByIsActiveTrue();

    @Query(value = "    SELECT org.*    " +
                   "    FROM master.purchasing_org org     " +
                   "    INNER JOIN auth.user_org uo    " +
                   "    ON 1=1    " +
                   "    AND org.id = uo.org_id" +
                   "    INNER JOIN auth.user_accounts ua     " +
                   "    ON 1=1    " +
                   "    AND ua.email = :email "
            , nativeQuery = true)
    List<PurchasingOrgEntity> findByUserEmail(String email);
}
