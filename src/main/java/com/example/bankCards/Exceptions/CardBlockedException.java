package com.example.bankCards.Exceptions;

public class CardBlockedException extends Throwable {
    public CardBlockedException(String recipientCardIsBlocked) {
        super("Карта заблокирована ");
    }
}
