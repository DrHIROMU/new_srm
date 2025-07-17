package com.advantech.srm.persistence.repository.main.account;

import com.advantech.srm.persistence.entity.main.account.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    public UserAccount findByEmail(String email);
}
