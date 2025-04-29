package com.example.bankCards.Controllers;

import com.example.bankCards.Dto.BankCardDto;
import com.example.bankCards.Dto.TransactionDto;
import com.example.bankCards.Dto.UserDto;
import com.example.bankCards.Exceptions.CardBlockedException;
import com.example.bankCards.Exceptions.InsufficientFundsException;
import com.example.bankCards.Generators.CardNumberGenerator;
import com.example.bankCards.Models.BankCard;
import com.example.bankCards.Models.Transactionn;
import com.example.bankCards.Models.User;
import com.example.bankCards.Repository.BankCardsRepository;
import com.example.bankCards.Repository.JwtTokenRepository;
import com.example.bankCards.Repository.TransactionRepository;
import com.example.bankCards.Repository.UserRepository;
import com.example.bankCards.Services.BankCardService;
import com.example.bankCards.Services.TransactionService;
import com.example.bankCards.Services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/operations")
public class BankCardsController {


    @Autowired
    private BCryptPasswordEncoder Encoder;
    @Autowired
    private JwtTokenRepository tokenRepository;

    @Autowired
    BankCardService bankCardService;
    @Autowired
    private UserService userService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    JwtTokenRepository jwtTokenRepository;


    @Operation(summary = "Возвращается все карты для просмотра админом")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getCards")
    public ResponseEntity<List<BankCard>> showAllCards()
    {
        var cards=bankCardService.getAllCards();
//        for(var k:cards)
//        {
//            k.setCardNumber(k.maskedCardNumber());
//        }
       return new ResponseEntity<>(cards,HttpStatus.OK);
    }



    @Operation(summary = "Создаёт новую карту банковскую карту ")
    @PostMapping("/createNewCard")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> createNewCard(@RequestBody BankCardDto cardDto)
    {

        try {
        var user =userService.findUserByemail(cardDto.getEmail());
        if(user.isEmpty())
            return new ResponseEntity<>("User Not Exsist", HttpStatus.BAD_REQUEST);
            String message=bankCardService.createNewCard(cardDto,user.get());
            return new ResponseEntity<>(message, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Устанавливает карте статус")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/setStatusCard")
    public ResponseEntity<String> setStatusCard(@RequestBody BankCardDto cardDto)
    {
        try
        {
            bankCardService.setCardStatus(cardDto);
            String message;
            switch (cardDto.getCardStatus())
            {
                case 1:
                    message="Card active";
                    break;
                case 2:
                    message="Card blocked";
                    break;
                case 3:
                    message="Card Expired";
                    break;
                default:
                    return new ResponseEntity<>("Inccorect Data",HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(message,HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

    @Operation(summary = "Устанавливает дневные и месячные лимиты для карты")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/addLimits")
    public ResponseEntity<String> addLimits(@RequestBody BankCardDto bankCardDto)
    {
        try {
            bankCardService.updateLimits(bankCardDto.getCardNumber(),200,200);
            return new ResponseEntity<>("Limits added",HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }


    @Operation(summary = "Возвращает для просмотра карты пользователя пользователю ")
    @PreAuthorize("hasRole('ROLE_USER')or hasRole('ROLE_ADMIN')")
    @GetMapping("/showUserCards")
    public ResponseEntity<List<BankCard>> showUserCards(@RequestBody BankCardDto bankCardDto,@RequestHeader("Authorization") String token)
    {
        var tokenJwt = token.substring(7);
        var jwtClaimEmail=jwtTokenRepository.extractAllClaims(tokenJwt).get("email", String.class);
        var user= userService.findUserByemail(jwtClaimEmail);
        BankCardDto cardDto=new BankCardDto();
        cardDto.setEmail(jwtClaimEmail);
        try {
           var userCards= bankCardService.getUserCards(cardDto);
            return new ResponseEntity<>(userCards,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Возвращает определённкую крату пользователю")
    @PreAuthorize("hasRole('ROLE_USER')or hasRole('ROLE_ADMIN')")
    @GetMapping("/showUserCard")
    public ResponseEntity<BankCard> showUserCard(@RequestBody BankCardDto bankCardDto,@RequestHeader("Authorization") String token )
    {
        var tokenJwt = token.substring(7);
        var jwtClaimEmail=jwtTokenRepository.extractAllClaims(tokenJwt).get("email", String.class);
        var user= userService.findUserByemail(jwtClaimEmail);
        BankCardDto cardDto=new BankCardDto();
        cardDto.setEmail(jwtClaimEmail);
        try {
            var userCard= bankCardService.getCard(cardDto);
            return new ResponseEntity<>(userCard,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Возвращает траназкции по карте для просомтра пользователю")
    @PreAuthorize("hasRole('ROLE_USER')or hasRole('ROLE_ADMIN')")
    @GetMapping("/seeTransactions")
    public ResponseEntity<List<Transactionn>> seeTransactions(@RequestBody BankCardDto bankCardDto)
    {
        var card = bankCardService.getCard(bankCardDto);

        if(card!=null)
        {
            var listTransaction=card.getTransactionHistory();
            return new ResponseEntity<>(listTransaction,HttpStatus.OK);
        }
        return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('ROLE_USER')or hasRole('ROLE_ADMIN')")
    @GetMapping("/seeTransactions2")
    public ResponseEntity<List<Transactionn>> specialTransatction(@RequestBody BankCardDto bankCardDto)
    {
        try {
            var card =bankCardService.getCard(bankCardDto);
            var allTransactions=transactionService.getAllTransactionsForCard(card);
            if(allTransactions.isEmpty())
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(allTransactions, HttpStatus.OK);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Operation(summary = "Операция перевода между картами")
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')or hasRole('ROLE_ADMIN')")
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransactionDto transactionDto)throws InsufficientFundsException, CardBlockedException
    {
        BankCardDto dto=new BankCardDto();
        BankCardDto cardDto=new BankCardDto();
        dto.setCardNumber(transactionDto.getSender());
        BankCard card1=bankCardService.getCard(dto);
        dto.setCardNumber(transactionDto.getRecipient());
        BankCard card2=bankCardService.getCard(dto);
        if(card1==null||card2==null)
              return new ResponseEntity<>("transaction cannot be completed",HttpStatus.BAD_REQUEST);
        try {
            transactionService.transferTransaction(card1,card2,transactionDto);
            var card = bankCardService.getCard(cardDto);
            return new ResponseEntity<>("transaction completed",HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Операция снятия денег с карты")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping("/withdrawing")
    public ResponseEntity<String> withdrawing (@RequestBody TransactionDto transationDto) throws InsufficientFundsException, CardBlockedException
    {
        BankCardDto cardDto=new BankCardDto();
        cardDto.setCardNumber(transationDto.getSender());
        try {
            var card = bankCardService.getCard(cardDto);var nn =transactionService.withdrawMoney(card,transationDto);
            return new ResponseEntity<>("transaction completed",HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }



}
