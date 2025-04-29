package com.example.bankCards.Models;

import com.example.bankCards.Crypto.StringEncryptorConverter;
import com.example.bankCards.Crypto.IntegerEncryptorConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Data
@Builder
public class Transactionn {




    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id ;
    @Convert(converter = StringEncryptorConverter.class)
    private String  Sender;
    @Convert(converter = StringEncryptorConverter.class)
    private String Recipient;
    @Convert(converter = IntegerEncryptorConverter.class)
    private int Amount;
    @Convert(converter = StringEncryptorConverter.class)
    private String Description;
    private LocalDateTime dateOfTransation;

    public Transactionn(String sender, String recipient, int amount, String description, BankCard bankCard) {
        this.Sender = sender;
        this.Recipient = recipient;
        this.Amount = amount;
        this.Description = description;
        this.bankCard = bankCard;
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "bank_card_id")
    private BankCard bankCard;



}
