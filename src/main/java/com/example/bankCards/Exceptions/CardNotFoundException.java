package com.example.bankCards.Exceptions;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(String cardNumber) {
        super("Транзакция не может быть соверешена ");
    }
}
