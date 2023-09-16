package dev.scaraz.mars.app.administration.config.event.app;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CacheExpiredEvent extends ApplicationEvent {

    private final String key;
    private final String namespace;
    private final String value;


    public CacheExpiredEvent(Object source,
                             String key,
                             String namespace,
                             String value
    ) {
        super(source);
        this.key = key;
        this.namespace = namespace;
        this.value = value;
    }
}
