package com.advantech.srm.auth.service;

import com.advantech.srm.persistence.entity.main.auth.UserAccountEntity;
import java.util.Optional;

public interface SupplierAccountService {
  Optional<UserAccountEntity> findActiveByEmail(String email);
}
