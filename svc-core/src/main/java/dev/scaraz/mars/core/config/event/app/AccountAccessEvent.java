package dev.scaraz.mars.core.config.event.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class AccountAccessEvent extends ApplicationEvent {

    private static ApplicationEventPublisher publisher;
    private static ObjectMapper objectMapper;

    private final String user;

    private final String details;

    public AccountAccessEvent(String type, String user, String details) {
        super(type);
        this.user = user;
        this.details = details;
    }

    public String getType() {
        return getSource();
    }

    @Override
    public String getSource() {
        return (String) super.getSource();
    }

    public static void notify(String type, String user, Object details) {
        try {
            if (details != null)
                publisher.publishEvent(new AccountAccessEvent(type, user, objectMapper.writeValueAsString(details)));
            else
                publisher.publishEvent(new AccountAccessEvent(type, user, null));
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void notify(String type, String user, Map<String, Object> details) {
        notify(type, user, (Object) details);
    }

    public static DetailBuilder details(String type, String user) {
        return new DetailBuilder(type, user);
    }


    public static void setObectMapper(ObjectMapper objectMapper) {
        AccountAccessEvent.objectMapper = objectMapper;
    }

    public static void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        AccountAccessEvent.publisher = eventPublisher;
    }

    public static class DetailBuilder {
        private final Map<String, Object> storage = new LinkedHashMap<>();
        private final String type;
        private final String user;

        private DetailBuilder(String type, String user) {
            this.type = type;
            this.user = user;
        }

        public DetailBuilder put(String key, Object value) {
            storage.put(key, value);
            return this;
        }

        public DetailBuilder source(Object value) {
            return put("source", value);
        }

        public void publish() {
            AccountAccessEvent.notify(type, user, storage);
        }
    }
}
