package com.hwsinha.carbid.group;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface BuyGroupRepository extends JpaRepository<BuyGroup,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BuyGroup> findById(long id);
}
