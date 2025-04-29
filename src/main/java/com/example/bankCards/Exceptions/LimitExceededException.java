package com.example.bankCards.Exceptions;


public class LimitExceededException extends RuntimeException {
    public LimitExceededException(String cardNumber) {
        super("Карта с номером " + cardNumber + " не найдена");
    }
}