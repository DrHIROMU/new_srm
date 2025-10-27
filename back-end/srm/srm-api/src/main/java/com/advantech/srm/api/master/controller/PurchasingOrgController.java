package com.advantech.srm.api.master.controller;

import com.advantech.srm.api.master.service.PurchasingOrgService;
import com.advantech.srm.common.dto.master.PurchasingOrgDto;
import com.advantech.srm.persistence.entity.main.master.PurchasingOrgEntity;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "查詢SRM使用中的採購組織(Purchasing Org)", description = "查詢啟用的採購組織,在沒有限制必須有採購組織權限的功能下,可用於查詢條件或是做為新增流程單據可選擇的選項等")
    public List<PurchasingOrgDto> findActivePurchasingOrgs() {
        return purchasingOrgService.findActivePurchasingOrgs();
    }

    @GetMapping("/{email}")
    @Operation(summary = "根據email查詢使用者所擁有的採購組織權限", description = "查出使用者所擁有的採購組織權限可用於查詢條件,或是做為新增流程單據可選擇的選項等")
    public List<PurchasingOrgDto> findPurchasingOrgsByUserEmail(@PathVariable String email) {
        return purchasingOrgService.findPurchasingOrgsByUserEmail(email);
    }
}
