package com.example.bankCards.Generators;

import org.springframework.context.annotation.Bean;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class CardNumberGenerator {
    private static final Set<String> generatedNumbers = new HashSet<>();
    private static final Random random = new Random();
    private static final String BIN = "4"; 

    public static synchronized String generateUniqueValidCardNumber() {
        String cardNumber;
        do {
            cardNumber = generateValidCardNumber();
        } while (!generatedNumbers.add(cardNumber));

        return cardNumber;
    }

    private static String generateValidCardNumber() {
        StringBuilder sb = new StringBuilder(BIN);
        for (int i = 0; i < 14; i++) {
            sb.append(random.nextInt(10));
        }

        String partialNumber = sb.toString();
        int checkDigit = calculateLuhnCheckDigit(partialNumber);
        return partialNumber + checkDigit;
    }

    private static int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            sum += digit;
            alternate = !alternate;
        }
        return (10 - (sum % 10)) % 10;
    }
}
