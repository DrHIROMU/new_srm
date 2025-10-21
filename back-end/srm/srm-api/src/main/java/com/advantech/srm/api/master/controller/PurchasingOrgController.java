package com.advantech.srm.api.master.controller;

import com.advantech.srm.api.master.service.PurchasingOrgService;
import com.advantech.srm.persistence.entity.main.master.PurchasingOrgEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/purchasing-orgs")
@RestController
@Tag(name = "Purchasing Org", description = "採購組織資訊 API")
@RequiredArgsConstructor
public class PurchasingOrgController {
    private final PurchasingOrgService purchasingOrgService;

    @GetMapping("/active")
    public List<PurchasingOrgEntity> findActivePurchasingOrgs() {
        return purchasingOrgService.findActivePurchasingOrgs();
    }

    @GetMapping("/{email}")
    public List<PurchasingOrgEntity> findPurchasingOrgsByUserEmail(@PathVariable String email) {
        return purchasingOrgService.findPurchasingOrgsByUserEmail(email);
    }
}
