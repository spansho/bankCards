package com.example.bankCards;
import com.example.bankCards.Dto.BankCardDto;
import com.example.bankCards.Dto.TransactionDto;
import com.example.bankCards.Dto.UserDto;
import com.example.bankCards.Exceptions.CardBlockedException;
import com.example.bankCards.Exceptions.InsufficientFundsException;
import com.example.bankCards.Exceptions.LimitExceededException;
import com.example.bankCards.Models.BankCard;
import com.example.bankCards.Repository.BankCardsRepository;
import com.example.bankCards.Services.BankCardService;
import com.example.bankCards.Services.TransactionService;
import com.example.bankCards.Services.UserService;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TranactionServiceTest {
    @Autowired
    private BankCardsRepository bankCardsRepository;

    @Autowired
    private BankCardService bankCardService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionService transactionService;

    @Test
    @Transactional
    void  transferTransaction_CardsBalanceShouldBeCorrect() throws InsufficientFundsException, CardBlockedException, InvalidTransactionException {
        try {
            UserDto userDto = new UserDto();
            userDto.setEmail("testexample@gmail.com");
            userDto.setRole("USER");
            userDto.setPassword("11231455");
            userService.createUser(userDto);
            var userFromDb = userService.findUserByemail("testexample@gmail.com");

            BankCardDto bankCardDto = new BankCardDto();
            bankCardDto.setUser(userFromDb.get());
            bankCardDto.setDayLimit(4000);
            bankCardDto.setBalance(150000);
            bankCardDto.setCardStatus(1);
            bankCardDto.setMonthlyLimit(1500000);


            bankCardDto.setCardNumber(bankCardService.createNewCard(bankCardDto, userFromDb.get()).substring(0,16));
            var bankCard = bankCardService.getCard(bankCardDto);

            UserDto userDto2 = new UserDto();
            userDto.setEmail("testexample2@gmail.com");
            userDto.setRole("USER");
            userDto.setPassword("11231455");
            userService.createUser(userDto);
            var userFromDb2 = userService.findUserByemail("testexample@gmail.com");

            BankCardDto bankCardDto2 = new BankCardDto();
            bankCardDto2.setUser(userFromDb2.get());
            bankCardDto2.setCardStatus(1);
            bankCardDto2.setBalance(0);
            bankCardDto2.setDayLimit(15000);
            bankCardDto2.setMonthlyLimit(1500000);
            //bankCardDto2.setCardNumber("1234567891011466");
           bankCardDto2.setCardNumber(bankCardService.createNewCard(bankCardDto2, userFromDb2.get()).substring(0,16));
            var bankCard2 = bankCardService.getCard(bankCardDto2);

            TransactionDto transactionDto=new TransactionDto();
            transactionDto.setAmount(5000);
            transactionDto.setDescription("На др");
            transactionDto.setDateOfTransation(LocalDate.now());

            //var transaction =transactionService.performTransaction(bankCard,bankCard2,transactionDto);

            var bankCardSender = bankCardService.getCard(bankCardDto);
            var bankCardRecepient= bankCardService.getCard(bankCardDto2);

            assertThrows(LimitExceededException.class,()->{transactionService.transferTransaction(bankCardSender,bankCardRecepient,transactionDto);});
            assertEquals(150000,bankCardSender.getBalance());
            assertEquals(0,bankCardRecepient.getBalance());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }

    @Test
    @Transactional
    void  withDrawTransaction_AmmountBiggerThanDailyLimitTranascationShouldBeNoValid() throws InsufficientFundsException, CardBlockedException, InvalidTransactionException {
        try {
            UserDto userDto = new UserDto();
            userDto.setEmail("testexample@gmail.com");
            userDto.setRole("ADMIN");
            userDto.setPassword("11231455");
            userService.createUser(userDto);
            var userFromDb = userService.findUserByemail("testexample@gmail.com");

            BankCardDto bankCardDto = new BankCardDto();
            bankCardDto.setUser(userFromDb.get());
            bankCardDto.setDayLimit(4000);
            bankCardDto.setBalance(5000);
            bankCardDto.setCardStatus(1);
            bankCardDto.setMonthlyLimit(1500000);

          //  bankCardService.createNewCard(bankCardDto, userFromDb.get());
            bankCardDto.setCardNumber(bankCardService.createNewCard(bankCardDto, userFromDb.get()).substring(0,16));
            var bankCard = bankCardService.getCard(bankCardDto);


            TransactionDto transactionDto=new TransactionDto();
            transactionDto.setAmount(5000);

            transactionService.withdrawMoney(bankCard,transactionDto);

            bankCard = bankCardService.getCard(bankCardDto);

            assertEquals(5000,bankCard.getBalance());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }

    @Test
    @Transactional
    void  withDrawTransaction_AmmountBiggerThanBalanceTranascationShouldBeNoValid() throws InsufficientFundsException, CardBlockedException, InvalidTransactionException {
        UserDto userDto = new UserDto();
        userDto.setEmail("testexample@gmail.com");
        userDto.setRole("ADMIN");
        userDto.setPassword("11231455");
        userService.createUser(userDto);
        var userFromDb = userService.findUserByemail("testexample@gmail.com");

        BankCardDto bankCardDto = new BankCardDto();
        bankCardDto.setUser(userFromDb.get());
        bankCardDto.setDayLimit(6000);
        bankCardDto.setBalance(5000);
        bankCardDto.setCardStatus(1);
        bankCardDto.setMonthlyLimit(1500000);
        bankCardDto.setCardNumber(bankCardService.createNewCard(bankCardDto, userFromDb.get()).substring(0,16));
        final BankCard bankCard = bankCardService.getCard(bankCardDto);
            TransactionDto transactionDto=new TransactionDto();
            transactionDto.setAmount(5500);
            assertThrows(Exception.class,()->{transactionService.withdrawMoney(bankCard,transactionDto);});
    }

    @Test
    @Transactional
    void  withDrawTransaction_AmmountMoneyInNewMonthTranascationShouldBeValid() throws InsufficientFundsException, CardBlockedException, Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("testexample@gmail.com");
        userDto.setRole("ADMIN");
        userDto.setPassword("11231455");
        userService.createUser(userDto);
        var userFromDb = userService.findUserByemail("testexample@gmail.com");


        BankCardDto bankCardDto = new BankCardDto();
        bankCardDto.setUser(userFromDb.get());
        bankCardDto.setDayLimit(2000);
        bankCardDto.setBalance(6000);
        bankCardDto.setCardStatus(1);
        bankCardDto.setMonthlyLimit(4000);
        bankCardDto.setCardNumber(bankCardService.createNewCard(bankCardDto, userFromDb.get()).substring(0,16));
        bankCardService.createNewCard(bankCardDto, userFromDb.get());
        BankCard bankCard = bankCardService.getCard(bankCardDto);
        TransactionDto transactionDto=new TransactionDto();
        transactionDto.setAmount(2000);
        transactionDto.setDateOfTransation(LocalDate.now());
        transactionService.withdrawMoney(bankCard,transactionDto);
        transactionDto.setDateOfTransation(LocalDate.now().plusDays(1));
        bankCardService.saveUpdateCard(bankCard);
        bankCard = bankCardService.getCard(bankCardDto);
        transactionService.withdrawMoney(bankCard,transactionDto);
        bankCardService.saveUpdateCard(bankCard);
        bankCard = bankCardService.getCard(bankCardDto);
        transactionDto.setDateOfTransation(LocalDate.now().plusMonths(1));
        transactionService.withdrawMoney(bankCard,transactionDto);
        //transactionService.withdrawMoney(bankCard,transactionDto);
        assertEquals(0,bankCard.getBalance());
        assertEquals(transactionDto.getDateOfTransation(),bankCard.getLastDailyReset());
        //assertThrows(InsufficientFundsException.class,()->{transactionService.withdrawMoney(bankCard,transactionDto);});
    }

    @Test
    @Transactional
    void  withDrawTransaction_AmmountMoneyInNewDayTranascationShouldBeValid() throws InsufficientFundsException, CardBlockedException, Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("testexample@gmail.com");
        userDto.setRole("ADMIN");
        userDto.setPassword("11231455");
        userService.createUser(userDto);
        var userFromDb = userService.findUserByemail("testexample@gmail.com");


        BankCardDto bankCardDto = new BankCardDto();
        bankCardDto.setUser(userFromDb.get());
        bankCardDto.setDayLimit(5000);
        bankCardDto.setBalance(10000);
        bankCardDto.setCardStatus(1);
        bankCardDto.setMonthlyLimit(1500000);

        bankCardDto.setCardNumber(bankCardService.createNewCard(bankCardDto, userFromDb.get()).substring(0,16));
        BankCard bankCard = bankCardService.getCard(bankCardDto);
        TransactionDto transactionDto=new TransactionDto();
        transactionDto.setAmount(5000);
        transactionDto.setDateOfTransation(LocalDate.now());
        transactionService.withdrawMoney(bankCard,transactionDto);
        transactionDto.setDateOfTransation(LocalDate.now().plusDays(1));
        bankCardService.saveUpdateCard(bankCard);
        bankCard = bankCardService.getCard(bankCardDto);
        //transactionService.withdrawMoney(bankCard,transactionDto);
        transactionService.withdrawMoney(bankCard,transactionDto);
        assertEquals(0,bankCard.getBalance());
        assertEquals(transactionDto.getDateOfTransation(),bankCard.getLastDailyReset());
        //assertThrows(InsufficientFundsException.class,()->{transactionService.withdrawMoney(bankCard,transactionDto);});
    }


}
