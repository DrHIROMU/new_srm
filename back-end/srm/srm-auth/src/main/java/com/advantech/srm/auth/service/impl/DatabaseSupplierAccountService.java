package com.advantech.srm.auth.service.impl;

import com.advantech.srm.auth.service.SupplierAccountService;
import com.advantech.srm.domain.enums.UserAccountStatusEnum;
import com.advantech.srm.persistence.entity.main.auth.UserAccountEntity;
import com.advantech.srm.persistence.repository.main.account.UserAccountRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DatabaseSupplierAccountService implements SupplierAccountService {

  private final UserAccountRepository repository;

  public DatabaseSupplierAccountService(UserAccountRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<UserAccountEntity> findActiveByEmail(String email) {
    return Optional.ofNullable(repository.findByEmail(email))
        .filter(account -> account.getAccountStatus() == UserAccountStatusEnum.ACTIVE);
  }
}
