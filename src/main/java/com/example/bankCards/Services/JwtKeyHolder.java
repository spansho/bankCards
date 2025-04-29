package com.example.bankCards.Services;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Base64;

public final class JwtKeyHolder {

    private static final String BASE64_KEY = "U2VjcmV0S2V5MTIzNDU2Nzg5MEFCQ0RFRkdISUpLTE1OT1BRUlNUVVZXWFla";
    public static final SecretKey KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(BASE64_KEY));
    public static SecretKey getKey() {
        return KEY;
    }
}