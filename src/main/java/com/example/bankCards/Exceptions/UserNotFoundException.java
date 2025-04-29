package com.example.bankCards.Exceptions;

public class UserNotFoundException extends Throwable {
    public UserNotFoundException(String email) {
        super("Пользователь c почтой "+email+" не найден ");
    }
}