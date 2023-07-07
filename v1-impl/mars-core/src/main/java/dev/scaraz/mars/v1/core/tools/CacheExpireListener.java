package dev.scaraz.mars.v1.core.tools;

public interface CacheExpireListener {
    String getNamespace();

    void onExpired(String key);
}
