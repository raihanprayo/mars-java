package dev.scaraz.mars.security;

import dev.scaraz.mars.security.credential.CredentialEncoderSupplier;
import dev.scaraz.mars.security.credential.CredentialStructure;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarsPasswordEncoder implements PasswordEncoder {

    private static final int DEFAULT_PASSWORD_STRENGTH = 16;
    private static Supplier<Integer> PASSWORD_STRENGTH = () -> DEFAULT_PASSWORD_STRENGTH;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "(?<algo>:[a-zA-Z0-9-]):::" +
                    "(?<secret>:.+)?:::" +
                    "(?<iteration>:\\d+)?:::" +
                    "(?<password>:.*)");

    public static final Map<String, CredentialEncoderSupplier> ENCODERS = new LinkedHashMap<>();

    static {
        ENCODERS.put("bcrypt", CredentialEncoderSupplier.BCRYPT);
        ENCODERS.put("ldap", CredentialEncoderSupplier.LDAP);
        ENCODERS.put("pbkdf2", CredentialEncoderSupplier.PBKDF2);
        ENCODERS.put("scrypt", CredentialEncoderSupplier.SCRYPT);
    }

    public Set<String> availableAlgorithm() {
        return ENCODERS.keySet();
    }

    @Override
    public String encode(CharSequence rawPassword) {
        DecodedRawPassword md = getPasswordMetadata(rawPassword.toString());
        return getEncoder(md).encode(md.password);
    }

    public String encode(CredentialStructure credential) {
        return getEncoder(credential).encode(credential.getPassword());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        DecodedRawPassword md = getPasswordMetadata(encodedPassword);
        return getEncoder(md)
                .matches(rawPassword, md.password);
    }

    public boolean matches(CharSequence rawPassword, CredentialStructure credential) {
        return getEncoder(credential)
                .matches(rawPassword, credential.getPassword());
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        DecodedRawPassword md = getPasswordMetadata(encodedPassword);
        return getEncoder(md).upgradeEncoding(md.password);
    }

    private DecodedRawPassword getPasswordMetadata(String password) {
        DecodedRawPassword d = new DecodedRawPassword();

        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        d.algo = matcher.group("algo");
        d.password = matcher.group("password");

        if (StringUtils.isNoneBlank(matcher.group("secret")))
            d.secret = matcher.group("secret");

        if (StringUtils.isNoneBlank(matcher.group("iteration")))
            d.hashIteration = Integer.parseInt(matcher.group("iteration"));

        String[] algo = d.algo.split("-");
        if (algo.length > 1) {
            d.algo = algo[0];
            d.subAlgo = d.algo.substring((algo[0] + "-").length());
        }
        return d.validate();
    }

    private PasswordEncoder getEncoder(DecodedRawPassword md) {
        String algo = StringUtils.isNoneBlank(md.subAlgo) ? md.subAlgo : md.algo;
        return ENCODERS.get(md.algo)
                .build(algo, md.secret, md.hashIteration);
    }

    private PasswordEncoder getEncoder(CredentialStructure cr) {
        DecodedRawPassword md = new DecodedRawPassword();
        md.secret = cr.getSecret();
        md.password = cr.getPassword();
        md.hashIteration = cr.getHashIteration();

        String[] algo = cr.getAlgorithm().split("-");
        if (algo.length > 1) {
            md.algo = algo[0];
            md.subAlgo = algo[0].substring((algo[0] + "-").length());
        }
        else {
            md.algo = algo[0];
        }
        return getEncoder(md);
    }

    public static void setPasswordStrength(int passwordStrength) {
        setPasswordStrength(() -> passwordStrength);
    }

    public static void setPasswordStrength(Supplier<Integer> passwordStrength) {
        Integer i = PASSWORD_STRENGTH.get();
        if (i == DEFAULT_PASSWORD_STRENGTH)
            PASSWORD_STRENGTH = passwordStrength;
    }

    public static int getPasswordStrength() {
        return PASSWORD_STRENGTH.get();
    }

    private static class DecodedRawPassword {
        String algo;
        String subAlgo;
        String secret;
        Integer hashIteration;

        String password;

        public DecodedRawPassword validate() {
            Assert.isTrue(StringUtils.isBlank(algo), "algorithm cannot be null or empty");
            Assert.isTrue(StringUtils.isBlank(password), "actual password cannot be null or empty");
            return this;
        }
    }

}
