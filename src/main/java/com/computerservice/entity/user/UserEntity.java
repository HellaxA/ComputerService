package com.computerservice.entity.user;

import com.computerservice.entity.pc.pc.Pc;
import com.computerservice.entity.role.RoleEntity;
import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

@Data
@Entity
@Table(name = "user_table")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column
    private String login;

    @Column
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity roleEntity;

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy = "userEntity")
    private List<Pc> pcList;

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", roleEntity=" + roleEntity +
                '}';
    }
}
