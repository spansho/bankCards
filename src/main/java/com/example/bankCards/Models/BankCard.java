package com.example.bankCards.Models;
import com.example.bankCards.Crypto.IntegerEncryptorConverter;
import com.example.bankCards.Crypto.StringEncryptorConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
public class BankCard {

    @Getter
    @Id @GeneratedValue
    @Convert(converter = IntegerEncryptorConverter.class)
    private int id;
    @Getter @Setter
    @Convert(converter = StringEncryptorConverter.class)
    private String cardNumber;
    @Convert(converter = StringEncryptorConverter.class)
    @Getter @Setter
    private String Owner;
    @Getter @Setter
    private  LocalDate ValidityPeriod;
    @Getter @Setter
    @Convert(converter = IntegerEncryptorConverter.class)
    private int CardStatus;
    @Getter @Setter
    @Convert(converter = IntegerEncryptorConverter.class)
    private int Balance;

    @Getter @Setter
    @Convert(converter = IntegerEncryptorConverter.class)
    private int dailyUsed; // Использовано за день

    @Getter @Setter
    @Convert(converter = IntegerEncryptorConverter.class)
    private int monthlyUsed; // Использовано за месяц

    @Getter @Setter
    @Convert(converter = IntegerEncryptorConverter.class)
    private int dailyLimit; // Дневной лимит

    @Getter @Setter
    @Convert(converter = IntegerEncryptorConverter.class)
    private int monthlyLimit; // Месячный лимит

    @Getter @Setter
    private LocalDate lastDailyReset; // Дата последнего сброса дневного лимита

    @Getter @Setter

    private LocalDate lastMonthlyReset; // Дата последнего сброса месячного лимита

    @Getter @Setter
    @OneToMany(mappedBy = "bankCard", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Transactionn> transactionHistory = new ArrayList<>();




    @Getter @Setter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;


    public BankCard(String owner,LocalDate period)
    {
        this.Owner=owner;
        this.Balance=0;
        this.ValidityPeriod=period;
        this.CardStatus=0;
        this.user=null;
        this.dailyUsed=0;
        this.monthlyUsed=0;
        transactionHistory =new ArrayList<>();
    }

    public boolean canPerformTransaction(int amount) {
        LocalDate today = LocalDate.now();

        if (lastDailyReset == null || !lastDailyReset.equals(today)) {
            dailyUsed = 0;
            lastDailyReset = today;
        }

        // Сброс месячного лимита, если наступил новый месяц
        if (lastMonthlyReset == null || lastMonthlyReset.getMonth() != today.getMonth()) {
            monthlyUsed = 0;
            lastMonthlyReset = today;
        }

        // Проверка лимитов
        if (dailyUsed + amount > dailyLimit) {
            return false; // Превышен дневной лимит
        }

        if (monthlyUsed + amount > monthlyLimit) {
            return false; // Превышен месячный лимит
        }

        return true;
    }

    public void updateLimits(int amount) {
        dailyUsed += amount;
        monthlyUsed += amount;
    }

    public String maskedCardNumber()
    {
        return "****-****-****-"+this.cardNumber.substring(12);
    }

    public boolean isActive() {
        if(getCardStatus()==1)
            return true;
        return false;
    }
}
