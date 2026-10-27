package com.hwsinha.carbid.bid;


import com.hwsinha.carbid.dealer.Dealer;
import com.hwsinha.carbid.group.BuyGroup;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "bids", indexes = @Index(name = "idx_bids_group_dealer", columnList = "group_id, dealer_id"))
@Getter
@Setter
@NoArgsConstructor
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false, updatable = false)
    private BuyGroup group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dealer_id", nullable = false, updatable = false)
    private Dealer dealer;

    @Column(nullable = false, precision = 12, scale = 2, updatable = false)
    private BigDecimal price;

    @Column(length = 500, updatable = false)
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }
}
