package com.hwsinha.carbid.dealer;

import com.hwsinha.carbid.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dealers")
@Getter
@Setter
@NoArgsConstructor
public class Dealer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String showroomName;

    @Column(nullable = false)
    private String city;

    @OneToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
