package com.example.bankCards.Crypto;

import jakarta.persistence.AttributeConverter;
import org.jasypt.encryption.StringEncryptor;

public class IntegerEncryptorConverter implements AttributeConverter<Integer, String> {

    private final StringEncryptor encryptor;

    public IntegerEncryptorConverter(StringEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public String convertToDatabaseColumn(Integer attribute) {
        if (attribute == null) {
            return null;
        }
        return encryptor.encrypt(String.valueOf(attribute));
    }

    @Override
    public Integer convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return Integer.valueOf(encryptor.decrypt(dbData));
    }
}