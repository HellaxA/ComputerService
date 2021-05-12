package com.computerservice.entity.resettoken;


import com.computerservice.entity.user.UserEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Data
@Table(name = "reset_token")
@Entity
@NoArgsConstructor
public class ResetToken {
    public static final int EXPIRATION = 3600 * 3 * 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "token")
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;

    @Column(name = "expiry_date")
    private Date expiryDate;

    public ResetToken(String token, UserEntity userEntity, Date expiryDate) {
        this.token = token;
        this.userEntity = userEntity;
        this.expiryDate = expiryDate;
    }

}
