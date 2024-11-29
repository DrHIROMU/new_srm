package com.advantech.srm.vendor.controller;

import com.advantech.srm.persistence.model.VendorMasterEntity;
import com.advantech.srm.persistence.repository.VendorMasterRepository;
import com.advantech.srm.vendor.service.VendorService;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/vendor")
public class VendorController {
    @Autowired
    private VendorService vendorService;

    @GetMapping("{id}")
    public ResponseEntity<VendorMasterEntity> findVendorById(@PathVariable Long id){
        return vendorService.findVendorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
