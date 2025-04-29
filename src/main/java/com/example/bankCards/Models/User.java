package com.example.bankCards.Models;

import com.example.bankCards.Crypto.StringEncryptorConverter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "app_user")
@Data
public class User {
    @Id @GeneratedValue
    private int id;


    @Convert(converter = StringEncryptorConverter.class)
    private String email;

    @Convert(converter = StringEncryptorConverter.class)
    private String role;
    @Convert(converter = StringEncryptorConverter.class)
    private String password;




    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<BankCard> listOfCard;
}
