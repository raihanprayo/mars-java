package dev.scaraz.mars.core.config.event.app;

import dev.scaraz.mars.core.domain.Config;
import org.springframework.context.ApplicationEvent;

public class ConfigUpdateEvent extends ApplicationEvent {

    private final Config config;

    public ConfigUpdateEvent(Config config) {
        super(config.getKey());
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }

    @Override
    public String getSource() {
        return (String) super.getSource();
    }

}
