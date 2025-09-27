package com.advantech.srm.persistence.repository.main.account;

import com.advantech.srm.persistence.entity.main.auth.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccountEntity, Long> {
    public Optional<UserAccountEntity> findByEmail(String email);
}
