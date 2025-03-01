package ru.project.task_service.keyGenerator;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Base64;

public class SecretKeyGenerator {
    @Test
    public void keyGenerator() {
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        String key = DatatypeConverter.printHexBinary(secretKey.getEncoded());
        System.out.println("Secret key : " + key);
    }
}
