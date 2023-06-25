package dev.scaraz.mars.security.credential;

import dev.scaraz.mars.security.MarsPasswordEncoder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.security.SecureRandom;

public interface CredentialEncoderSupplier {

    PasswordEncoder build(String algo, String secret, Integer hashIteration);

    CredentialEncoderSupplier BCRYPT = (algo, secret, hashIteration) -> {
        if (StringUtils.isBlank(secret)) return new BCryptPasswordEncoder(MarsPasswordEncoder.getPasswordStrength());
        SecureRandom rand = new SecureRandom(secret.getBytes());
        return new BCryptPasswordEncoder(MarsPasswordEncoder.getPasswordStrength(), rand);
    };

    CredentialEncoderSupplier LDAP = (algo, secret, hashIteration) ->
            new LdapShaPasswordEncoder(KeyGenerators.secureRandom(MarsPasswordEncoder.getPasswordStrength()));

    CredentialEncoderSupplier PBKDF2 = (algo, secret, hashIteration) -> {
        Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(
                secret,
                MarsPasswordEncoder.getPasswordStrength(),
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

    CredentialEncoderSupplier SCRYPT = (algo, secret, hashIteration) ->
            new SCryptPasswordEncoder(16384, 8, 1, secret.length(), MarsPasswordEncoder.getPasswordStrength());

}
