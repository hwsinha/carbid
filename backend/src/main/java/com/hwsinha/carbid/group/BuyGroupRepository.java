package com.hwsinha.carbid.group;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BuyGroupRepository extends JpaRepository<BuyGroup,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BuyGroup> findById(long id);

    List<BuyGroup> findByStatusAndDeadlineBefore(GroupStatus status, Instant now);
    List<BuyGroup> findByStatusAndBiddingDeadlineBefore(GroupStatus status, Instant now);

    @EntityGraph(attributePaths = {"car"})
    Page<BuyGroup> findByCityIgnoreCaseAndStatus(String city, GroupStatus status, Pageable pageable);
}
