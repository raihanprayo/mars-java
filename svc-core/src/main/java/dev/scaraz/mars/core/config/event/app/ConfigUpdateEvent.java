package dev.scaraz.mars.core.config.event.app;

import dev.scaraz.mars.core.domain.Config;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ConfigUpdateEvent extends ApplicationEvent {

    @Getter
    private final Config config;

    @Getter
    private final boolean valueChanged;

    public ConfigUpdateEvent(Config config, boolean valueChanged) {
        super(config.getKey());
        this.config = config;
        this.valueChanged = valueChanged;
    }

    public boolean is(String configKey) {
        return getSource().equals(configKey);
    }

    @Override
    public String getSource() {
        return (String) super.getSource();
    }

}
