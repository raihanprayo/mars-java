package dev.scaraz.mars.core.v2.config.security.credential;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.security.SecureRandom;

@FunctionalInterface
public interface EncoderSupplier {
    int DEFAULT_STRENGTH = 16;

    PasswordEncoder build(String algo, String secret, Integer hashIteration);

    EncoderSupplier BCRYPT = (algo, secret, hashIteration) -> {
        if (StringUtils.isBlank(secret)) return new BCryptPasswordEncoder(DEFAULT_STRENGTH);
        SecureRandom rand = new SecureRandom(secret.getBytes());
        return new BCryptPasswordEncoder(DEFAULT_STRENGTH, rand);
    };

    EncoderSupplier LDAP = (algo, secret, hashIteration) ->
            new LdapShaPasswordEncoder(KeyGenerators.secureRandom(DEFAULT_STRENGTH));

    EncoderSupplier PBKDF2 = (algo, secret, hashIteration) -> {
        Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(
                secret,
                DEFAULT_STRENGTH,
                hashIteration,
                256
        );

        switch (algo) {
            case "sha1":
                encoder.setAlgorithm(Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1);
                break;
            case "sha512":
                encoder.setAlgorithm(Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512);
                break;
            default:
                encoder.setAlgorithm(Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
                break;
        }

        return encoder;
    };

    EncoderSupplier SCRYPT = (algo, secret, hashIteration) ->
            new SCryptPasswordEncoder(16384, 8, 1, secret.length(), DEFAULT_STRENGTH);

}
