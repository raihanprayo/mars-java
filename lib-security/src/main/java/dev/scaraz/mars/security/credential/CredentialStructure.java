package dev.scaraz.mars.security.credential;

public interface CredentialStructure {
    String getAlgorithm();

    String getSecret();

    Integer getHashIteration();

    String getPassword();
}
