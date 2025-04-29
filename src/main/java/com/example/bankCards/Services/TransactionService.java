package com.example.bankCards.Services;

import com.example.bankCards.Dto.TransactionDto;
import com.example.bankCards.Exceptions.CardBlockedException;
import com.example.bankCards.Exceptions.CardNotFoundException;
import com.example.bankCards.Exceptions.InsufficientFundsException;
import com.example.bankCards.Exceptions.LimitExceededException;
import com.example.bankCards.Models.BankCard;
import com.example.bankCards.Models.Transactionn;
import com.example.bankCards.Repository.BankCardsRepository;
import com.example.bankCards.Repository.TransactionRepository;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {
    private final BankCardsRepository bankCardsRepository;
    private final TransactionRepository transactionRepository;


    public List<Transactionn> getAllTransactions()
    {
        return transactionRepository.findAll();
    }

    public List<Transactionn> getAllTransactionsForCard(BankCard card)
    {
        var transactions=getAllTransactions();
        if(!transactions.isEmpty()){

            return transactions.stream().filter(transactionn -> transactionn.getSender().equals(card.getCardNumber())).toList();
        }
            return null;

    }


    public Transactionn transferTransaction(BankCard card1,BankCard card2, TransactionDto dto) throws Exception {
//
        updateDateOfLimits(card1,dto);
        try {
            validateTransaction(card1, card2, dto.getAmount());
            Transactionn transaction = createTransaction(dto, card1, card2);
            updateBalances(card1, card2, dto.getAmount());

            transactionRepository.save(transaction);
            return transaction;
        }
           catch (CardBlockedException | InsufficientFundsException |InvalidTransactionException e)
        {
           throw new Exception(e.getMessage());
        }

    }

    public Transactionn withdrawMoney(BankCard card,TransactionDto dto) throws InsufficientFundsException, CardBlockedException, Exception {


        try {
            updateDateOfLimits(card,dto);
            validateWithdrawal(card, dto.getAmount());

            Transactionn transaction = createWithdrawalTransaction(dto.getAmount(), card);
            //checkTransactionLimits(card, dto.getAmount());
            transaction.setDescription("WITHDRAWAL");
            card.setBalance(card.getBalance()-dto.getAmount());
            card.setDailyUsed(card.getDailyUsed()+dto.getAmount());
            card.setMonthlyUsed(card.getMonthlyUsed()+dto.getAmount());
            transactionRepository.save(transaction);
            return transaction;
        } catch (Exception e) {
            String errorMessage = String.format("Error  %s", e.getMessage());
            e = new Exception(errorMessage, e);
            throw e;
        }
        catch (InsufficientFundsException |CardBlockedException e)
        {
           throw new Exception(e.getMessage());
        }
    }

    private Transactionn createTransaction(TransactionDto dto, BankCard sender, BankCard recipient) {
        return Transactionn.builder()
                .Sender(sender.getCardNumber())
                .Recipient(recipient.getCardNumber())
                .Amount(dto.getAmount())
                .Description(dto.getDescription())
                .dateOfTransation(LocalDateTime.now())
                .bankCard(sender)
                .build();
    }

    private void updateBalances(BankCard sender, BankCard recipient, int amount) {
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);
        bankCardsRepository.saveAll(List.of(sender, recipient));
    }

    private Transactionn createWithdrawalTransaction(int Amount, BankCard card) {
        return Transactionn.builder()
                .Sender(card.getCardNumber())
                .Recipient("ATM_WITHDRAWAL")
                .Amount(Amount)
                .Description("Cash withdrawal")
                .dateOfTransation(LocalDateTime.now())
                .bankCard(card)
                .build();
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

        checkTransactionLimits(card, amount);
    }

    public void updateDateOfLimits(BankCard card,TransactionDto transactionDto)
    {
        if(transactionDto.getDateOfTransation().isAfter(card.getLastDailyReset()))
        {
            //&&card.getDailyUsed()==card.getDailyLimit()
            card.setLastDailyReset(transactionDto.getDateOfTransation());
            card.setDailyUsed(0);
        }
        if(transactionDto.getDateOfTransation().getMonthValue()!=(card.getLastMonthlyReset().getMonthValue()))
        {
            //&&card.getDailyUsed()==card.getDailyLimit()
            card.setLastMonthlyReset(transactionDto.getDateOfTransation());
            card.setMonthlyUsed(0);
        }
    }



}

