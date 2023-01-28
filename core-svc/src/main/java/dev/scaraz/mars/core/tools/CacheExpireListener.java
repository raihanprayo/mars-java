package dev.scaraz.mars.core.tools;

public interface CacheExpireListener {
    String getNamespace();

    void onExpired(String key);
}
