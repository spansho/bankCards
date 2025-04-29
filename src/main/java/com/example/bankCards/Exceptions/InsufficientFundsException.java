package com.example.bankCards.Exceptions;

public class InsufficientFundsException extends Throwable {
    public InsufficientFundsException(String insufficientFunds) {
        super("Нехватка денег ");
    }
}
