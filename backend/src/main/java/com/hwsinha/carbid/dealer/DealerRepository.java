package com.hwsinha.carbid.dealer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DealerRepository extends JpaRepository<Dealer, Long> {

    Optional<Dealer> findByUserId(Long userId);

}
