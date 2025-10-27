package com.advantech.srm.api.master.service;

import com.advantech.srm.api.mapper.MasterDataMapper;
import com.advantech.srm.common.dto.master.PurchasingOrgDto;
import com.advantech.srm.persistence.entity.main.master.PurchasingOrgEntity;
import com.advantech.srm.persistence.repository.main.master.PurchasingOrgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchasingOrgService {
    private final PurchasingOrgRepository purchasingOrgRepository;
    private final MasterDataMapper masterDataMapper;

    public List<PurchasingOrgDto> findActivePurchasingOrgs(){
        return masterDataMapper.toPurchasingOrgDtoList(purchasingOrgRepository.findByIsActiveTrue());
    }

    public List<PurchasingOrgDto> findPurchasingOrgsByUserEmail(String email) {
        return masterDataMapper.toPurchasingOrgDtoList(purchasingOrgRepository.findByUserEmail(email));
    }
}
