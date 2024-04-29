package com.ksh.purchase.entity;

import com.ksh.purchase.entity.enums.UserType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Setter
    private List<Product> productList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Order> orderList = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Setter
    private Cart cart;

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

    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<UserType> authorities = List.of(this.getUserType());
        return authorities.stream()
                .map(type -> new SimpleGrantedAuthority(type.name()))
                .collect(Collectors.toList());
    }


}
