package com.ksh.purchase.entity;

import jakarta.persistence.*;
import lombok.*;

import static com.ksh.purchase.util.EncryptionUtil.*;

@Entity
@Table(name = "address")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Address extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    @Setter
    private User user;

    @Column(nullable = false)
    @Setter
    private String zipcode;
    @Column(nullable = false)
    @Setter
    private String address;
    @Setter
    private String detailedAddress;

    @Column(nullable = false)
    private boolean selected;

    public void setUser(User user) {
        this.user = user;
        this.getUser().getAddressList().add(this);
    }

    // 풀 주소
    public String getFullAddress() {
        return String.format("(%s) %s %s", decrypt(zipcode), decrypt(address), decrypt(detailedAddress));
    }

}
