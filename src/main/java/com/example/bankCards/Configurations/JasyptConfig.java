package com.example.bankCards.Configurations;

import com.example.bankCards.Crypto.StringEncryptorConverter;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {

    @Bean
    public StringEncryptorConverter stringEncryptorConverter(StringEncryptor encryptor) {
        return new StringEncryptorConverter(encryptor);
    }
}
