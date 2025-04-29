package com.example.bankCards;

import com.example.bankCards.Dto.BankCardDto;
import com.example.bankCards.Dto.UserDto;
import com.example.bankCards.Exceptions.CardNotFoundException;
import com.example.bankCards.Generators.CardNumberGenerator;
import com.example.bankCards.Models.BankCard;
import com.example.bankCards.Models.User;
import com.example.bankCards.Repository.BankCardsRepository;
import com.example.bankCards.Repository.UserRepository;
import com.example.bankCards.Services.BankCardService;
import com.example.bankCards.Services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class BankCardServiceTests {
    @Autowired
    private BankCardsRepository bankCardsRepository;


    @Autowired
    private BankCardService bankCardService;
    @Autowired
    private UserService userService;

    @Test
    @Transactional
    void  createNewCard_ShouldCreateCardWithCorrectParameters() {
        UserDto userDto=new UserDto();
        userDto.setEmail("testexample@gmail.com");
        userDto.setRole("ADMIN");
        userDto.setPassword("435263");
        userService.createUser(userDto);
        var userFromDb= userService.findUserByemail("testexample@gmail.com");
        BankCardDto bankCardDto = new BankCardDto();
        bankCardDto.setUser(userFromDb.get());
       bankCardDto.setCardNumber(bankCardService.createNewCard(bankCardDto,userFromDb.get()).substring(0,16));
        var savedCard = bankCardService.getCard(bankCardDto);
        assertNotNull(savedCard);
    }

    @Test
    @Transactional
    @ExtendWith(MockitoExtension.class)
    void activateCard_ShouldUpdateStatusFromDto() {
        UserDto userDto=new UserDto();
        userDto.setEmail("testexample@gmail.com");
        userDto.setRole("ADMIN");
        userDto.setPassword(CardNumberGenerator.generateUniqueValidCardNumber());
        userService.createUser(userDto);
        var userFromDb= userService.findUserByemail("testexample@gmail.com");
        BankCardDto bankCardDto = new BankCardDto();
        bankCardDto.setValidityPeriod(LocalDate.now().plusDays(10));
        bankCardService.createNewCard(bankCardDto,userFromDb.get());
        bankCardDto.setCardStatus(1);
        var savedCard = bankCardsRepository.findBycardNumber(bankCardDto.getCardNumber());
        bankCardDto.setCardNumber(bankCardService.createNewCard(bankCardDto,userFromDb.get()).substring(0,16));
        bankCardService.setCardStatus(bankCardDto);
        savedCard = bankCardsRepository.findBycardNumber(bankCardDto.getCardNumber());
        assertEquals(1, savedCard.get().getCardStatus());
    }

    @Test
    @Transactional
    @ExtendWith(MockitoExtension.class)
    void updateLimits_ShouldSetLimitsFromDto() {
        UserDto userDto=new UserDto();
        userDto.setEmail("testexample@gmail.com");
        userDto.setRole("ADMIN");
        userDto.setPassword(CardNumberGenerator.generateUniqueValidCardNumber());
        userService.createUser(userDto);
        var userFromDb= userService.findUserByemail("testexample@gmail.com");
        BankCardDto bankCardDto = new BankCardDto();
        bankCardDto.setValidityPeriod(LocalDate.now().plusDays(10));
        bankCardService.createNewCard(bankCardDto,userFromDb.get());
        bankCardDto.setCardNumber(bankCardService.createNewCard(bankCardDto,userFromDb.get()).substring(0,16));
        var savedCard = bankCardService.getCard(bankCardDto);
        assertEquals(0, savedCard.getDailyLimit());
        assertEquals(0, savedCard.getMonthlyLimit());
        bankCardService.updateLimits(bankCardDto.getCardNumber(),200,200);
        var savedCard2 = bankCardsRepository.findBycardNumber(bankCardDto.getCardNumber());
        assertEquals(200, savedCard2.get().getDailyLimit());
        assertEquals(200, savedCard2.get().getMonthlyLimit());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void whenCardNotFound_ShouldThrowException() {
        BankCardDto bankCardDto = new BankCardDto();
        bankCardDto.setCardNumber("1234567891011456");
        var savedCard = bankCardsRepository.findBycardNumber(bankCardDto.getCardNumber());

        assertEquals(savedCard.isEmpty(), true);
    }
}
