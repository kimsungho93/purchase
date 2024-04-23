package com.ksh.purchase.entity;

import com.ksh.purchase.entity.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @Setter
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Setter
    private List<Address> addressList = new ArrayList<>();

    @Column(nullable = false)
    @Setter
    private String phone;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(nullable = false, columnDefinition = "boolean default false")
    @Setter
    private boolean certificated;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isWithdrawal;
}
