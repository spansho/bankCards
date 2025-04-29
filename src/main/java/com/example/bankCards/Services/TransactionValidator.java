package com.example.bankCards.Services;

import com.example.bankCards.Exceptions.CardBlockedException;
import com.example.bankCards.Exceptions.InsufficientFundsException;
import com.example.bankCards.Exceptions.LimitExceededException;
import com.example.bankCards.Models.BankCard;
import jakarta.transaction.InvalidTransactionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionValidator {


    public void validateTransaction(BankCard sender, BankCard recipient, int amount) throws InvalidTransactionException, CardBlockedException, InsufficientFundsException {
        if (sender.getCardNumber().equals(recipient.getCardNumber())) {
            throw new InvalidTransactionException("Cannot transfer to the same card");
        }

        if (!sender.isActive()) {
            throw new CardBlockedException("Sender card is blocked");
        }

        if (!recipient.isActive()) {
            throw new CardBlockedException("Recipient card is blocked");
        }

        if (sender.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        checkTransactionLimits(sender, amount);
    }

    public void validateWithdrawal(BankCard card, int amount) throws CardBlockedException, InsufficientFundsException {
        if (!card.isActive()) {
            throw new CardBlockedException("Card is blocked");
        }

        if (card.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        checkWithdrawalLimits(card, amount);
    }

    public void checkTransactionLimits(BankCard card, int amount) {
        // Проверка дневных/месячных лимитов
        if (card.getDailyUsed() + amount > card.getDailyLimit()) {
            throw new LimitExceededException("Daily limit exceeded");
        }

        if (card.getMonthlyUsed() + amount > card.getMonthlyLimit()) {
            throw new LimitExceededException("Monthly limit exceeded");
        }
    }

    public void checkWithdrawalLimits(BankCard card, int amount) {
        checkTransactionLimits(card, amount);
    }

}