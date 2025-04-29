package com.example.bankCards.Repository;

import com.example.bankCards.Models.Transactionn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transactionn,Integer> {
    Optional<Transactionn> findById(int id);

}
