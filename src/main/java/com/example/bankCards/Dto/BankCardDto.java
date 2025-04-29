package com.example.bankCards.Dto;

import com.example.bankCards.Models.User;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

@Data
public class BankCardDto {
    @Getter
    public String CardNumber;
    @Getter
    public String Owner;
    @Getter
    public LocalDate ValidityPeriod;
    @Getter
    public String email;
    @Getter
    public int dayLimit;
    @Getter
    public int monthlyLimit;;
    @Getter
    public int CardStatus;
    @Getter
    User user;
    @Getter
    public int balance;


}
