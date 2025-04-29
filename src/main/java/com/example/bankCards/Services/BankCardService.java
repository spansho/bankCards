package com.example.bankCards.Services;

import com.example.bankCards.Dto.BankCardDto;
import com.example.bankCards.Exceptions.CardNotFoundException;
import com.example.bankCards.Generators.CardNumberGenerator;
import com.example.bankCards.Models.BankCard;
import com.example.bankCards.Models.Transactionn;
import com.example.bankCards.Models.User;
import com.example.bankCards.Repository.BankCardsRepository;
import com.example.bankCards.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankCardService {
    private final UserRepository userRepository;
    private final BankCardsRepository bankCardsRepository;


    @Transactional
    public String createNewCard(BankCardDto cardDto,User user) {
                    BankCard card = new BankCard();
                    card.setUser(user);
                    card.setBalance(cardDto.getBalance());
                    card.setDailyUsed(0);
                    card.setMonthlyUsed(0);
                    card.setOwner(cardDto.getOwner());
                    card.setLastMonthlyReset(LocalDate.now());
                    card.setLastDailyReset(LocalDate.now());
                    card.setDailyLimit(cardDto.getDayLimit());
                    card.setMonthlyLimit(cardDto.getMonthlyLimit());
                    card.setCardStatus(cardDto.getCardStatus());
                    card.setValidityPeriod(cardDto.getValidityPeriod());
                    card.setCardNumber(cardDto.getCardNumber());
                    card.setValidityPeriod(LocalDate.now().plusYears(3));
                    card.setCardNumber(CardNumberGenerator.generateUniqueValidCardNumber());
                    bankCardsRepository.save(card);
                    return card.getCardNumber()+" Карта"+" успешно создана";
    }


    public List<BankCard> getAllCards() {
       return bankCardsRepository.findAll();
    }
    private BankCard buildBankCard(BankCardDto dto, User user) {
        return BankCard.builder()
                .Owner(dto.getOwner())
                .ValidityPeriod(calculateValidityPeriod())
                .user(user)
                .cardNumber(CardNumberGenerator.generateUniqueValidCardNumber())
                .build();
    }

    private LocalDate calculateValidityPeriod() {
        return LocalDate.now().plusYears(3); // +3 года
    }

    @Transactional
    public void saveUpdateCard(BankCard card)
    {
        bankCardsRepository.save(card);
    }

    @Transactional
    public String activateCard(String cardNumber, int cardStatus) {
        BankCard card = bankCardsRepository.findBycardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException(cardNumber));

        card.setCardStatus(cardStatus);
        bankCardsRepository.save(card);
        return "Карта активирована";
    }

    @Transactional
    public String updateLimits(String cardNumber, int dailyLimit, int monthlyLimit) {
        BankCard card = bankCardsRepository.findBycardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException(cardNumber));

        card.setDailyLimit(dailyLimit);
        card.setMonthlyLimit(monthlyLimit);
        bankCardsRepository.save(card);
        return "Лимиты обновлены";
    }

    public BankCard getCardDetails(String cardNumber) {
        return bankCardsRepository.findBycardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException(cardNumber));
    }

    public List<Transactionn> getCardTransactions(String cardNumber) {
        BankCard card = bankCardsRepository.findBycardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException(cardNumber));
        return card.getTransactionHistory();
    }

    public List<BankCard> getUserCards(BankCardDto cardDto)
    {
        var userBanksCards=bankCardsRepository.findAll();
        var user= userRepository.findByemail(cardDto.getEmail()).orElseThrow(() -> new CardNotFoundException(cardDto.getCardNumber()));;
        return userBanksCards.stream().filter(card->Integer.valueOf(card.getUser().getId()).equals(user.getId())).toList();
    }

    public String setCardStatus(BankCardDto cardDto)
    {
        var card=getCard(cardDto);
        if(card!=null) {
            card.setCardStatus(cardDto.getCardStatus());
            bankCardsRepository.save(card);
            return "Статус карты установлен";
        }
        throw new CardNotFoundException(cardDto.getCardNumber());
    }

    public BankCard getCard(BankCardDto cardDto)
    {
        var userBanksCard=bankCardsRepository.findBycardNumber(cardDto.CardNumber);
        if(userBanksCard.isPresent())
        {
            return userBanksCard.get();
        }
        throw new CardNotFoundException(cardDto.getCardNumber());
    }



}
