package com.advantech.srm.api.master.service;

import com.advantech.srm.persistence.entity.main.master.PurchasingOrgEntity;
import com.advantech.srm.persistence.repository.main.master.PurchasingOrgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchasingOrgService {
    private final PurchasingOrgRepository purchasingOrgRepository;

    public List<PurchasingOrgEntity> findActivePurchasingOrgs(){
        return purchasingOrgRepository.findByIsActiveTrue();
    }

    public List<PurchasingOrgEntity> findPurchasingOrgsByUserEmail(String email) {
        return purchasingOrgRepository.findByUserEmail(email);
    }
}
