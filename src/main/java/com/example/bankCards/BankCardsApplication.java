package com.example.bankCards;

import com.example.bankCards.Filters.JwtTokenFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankCardsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankCardsApplication.class, args);
	}

}
