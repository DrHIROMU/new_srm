package com.advantech.srm.vendor.service;

import com.advantech.srm.persistence.model.VendorMasterEntity;
import com.advantech.srm.persistence.repository.VendorMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VendorService {
    @Autowired
    private VendorMasterRepository vendorMasterRepository;

    public Optional<VendorMasterEntity> findVendorById(Long id) {
        return vendorMasterRepository.findById(id);
    }
}
