package com.hwsinha.carbid.group;

import com.hwsinha.carbid.car.Car;
import com.hwsinha.carbid.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "buy_groups")
@Getter
@Setter
@NoArgsConstructor
public class BuyGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="car_id",  nullable = false)
    private Car car;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private int minMembers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupStatus status;

    @Column(nullable = false)
    private Instant deadline;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id",   nullable = false)
    private User createdBy;

    @Column(nullable = false,  updatable = false)
    private Instant createdAt;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }
}
