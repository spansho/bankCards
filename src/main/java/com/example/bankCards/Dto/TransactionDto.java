package com.example.bankCards.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    @Getter
    public String Sender;
    @Getter
    public String Recipient;
    @Getter
    public int Amount;
    @Getter
    public String Description;
    @Getter
    public LocalDate dateOfTransation;
}
