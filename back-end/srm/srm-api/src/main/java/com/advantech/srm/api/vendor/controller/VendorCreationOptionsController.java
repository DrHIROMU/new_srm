package com.advantech.srm.api.vendor.controller;

import com.advantech.srm.api.vendor.dto.CreationOptionsResponse;
import com.advantech.srm.api.vendor.dto.DropdownOptionDto;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vendors")
public class VendorCreationOptionsController {

  @GetMapping("/creation-options")
  @PreAuthorize("hasAuthority('vendors.read')")
  public ResponseEntity<CreationOptionsResponse> creationOptions() {
    CreationOptionsResponse response =
        new CreationOptionsResponse(
            List.of(
                new DropdownOptionDto("製造商", "MANUFACTURER"),
                new DropdownOptionDto("代理商", "DISTRIBUTOR"),
                new DropdownOptionDto("服務商", "SERVICE_PROVIDER")),
            List.of(
                new DropdownOptionDto("電子零件", "ELECTRONIC_COMPONENT"),
                new DropdownOptionDto("機構零件", "MECHANICAL_COMPONENT"),
                new DropdownOptionDto("軟體授權", "SOFTWARE_LICENSE")),
            List.of(
                new DropdownOptionDto("台灣", "TW"),
                new DropdownOptionDto("中國大陸", "CN"),
                new DropdownOptionDto("日本", "JP"),
                new DropdownOptionDto("美國", "US")),
            List.of(
                new DropdownOptionDto("5010 - 台灣採購中心", "5010"),
                new DropdownOptionDto("5020 - 中國採購中心", "5020"),
                new DropdownOptionDto("5030 - 歐洲採購中心", "5030")),
            List.of(
                new DropdownOptionDto("新台幣 (TWD)", "TWD"),
                new DropdownOptionDto("美元 (USD)", "USD"),
                new DropdownOptionDto("人民幣 (CNY)", "CNY")),
            List.of(
                new DropdownOptionDto("月結 30 天", "NET_30"),
                new DropdownOptionDto("月結 60 天", "NET_60"),
                new DropdownOptionDto("貨到付款", "COD")),
            List.of(
                new DropdownOptionDto("FOB", "FOB"),
                new DropdownOptionDto("CIF", "CIF"),
                new DropdownOptionDto("DAP", "DAP")),
            List.of(
                new DropdownOptionDto("一般採購", "STANDARD"),
                new DropdownOptionDto("委託代購", "CONSIGNMENT"),
                new DropdownOptionDto("維修服務", "SERVICE")));

    return ResponseEntity.ok(response);
  }
}
