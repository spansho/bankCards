package com.example.bankCards.Repository;

import com.example.bankCards.Models.BankCard;
import com.example.bankCards.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankCardsRepository extends JpaRepository<BankCard,Integer> {

    default Optional<BankCard> findBycardNumber(String CardNumber) {
        return findAll().stream()
                .filter(u -> u.getCardNumber().equals(CardNumber)) // Дешифровка через конвертер
                .findFirst();
    }

}
