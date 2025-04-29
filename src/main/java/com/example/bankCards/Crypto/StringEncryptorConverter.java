package com.example.bankCards.Crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jasypt.encryption.StringEncryptor;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

 // Автоматически применяется ко всем полям нужного типа
 @Converter
 public class StringEncryptorConverter implements AttributeConverter<String, String> {

     private final StringEncryptor encryptor;

     public StringEncryptorConverter(StringEncryptor encryptor) {
         this.encryptor = encryptor;
     }

     @Override
     public String convertToDatabaseColumn(String attribute) {
         return attribute != null ? encryptor.encrypt(attribute) : null;
     }

     @Override
     public String convertToEntityAttribute(String dbData) {
         return dbData != null ? encryptor.decrypt(dbData) : null;
     }
 }