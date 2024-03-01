package dev.scaraz.mars.telegram.model;

import dev.scaraz.mars.telegram.config.processor.TelegramProcessor;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import dev.scaraz.mars.telegram.util.enums.ProcessCycle;
import lombok.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TelegramProcessContext {

    private ProcessCycle cycle;

    private Update update;
    private TelegramHandler handler;
    private Object[] handlerArguments;
    private TelegramProcessor processor;
    private BotApiMethod<?> result;

    @Getter(AccessLevel.NONE)
    private final Map<String, Object> attributes = new LinkedHashMap<>();

    public boolean hasResult() {
        return result != null;
    }

    public long getId() {
        return update.getUpdateId();
    }

    public HandlerType getType() {
        return processor.type();
    }

    public TelegramProcessContext addAttribute(String key, Object value) {
        attributes.put(key, value);
        return this;
    }

    public TelegramProcessContext removeAttribute(String key) {
        attributes.remove(key);
        return this;
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public TelegramProcessContextBuilder toBuilder() {
        return new TelegramProcessContextBuilder(this);
    }

    public static TelegramProcessContextBuilder builder() {
        return new TelegramProcessContextBuilder();
    }

    public static class TelegramProcessContextBuilder {
        private ProcessCycle cycle;
        private Update update;
        private TelegramHandler handler;
        private Object[] handlerArguments;
        private TelegramProcessor processor;
        private BotApiMethod<?> result;

        private Map<String, Object> attributes = new LinkedHashMap<>();

        TelegramProcessContextBuilder() {
        }

        TelegramProcessContextBuilder(TelegramProcessContext ctx) {
            this.cycle = ctx.cycle;
            this.update = ctx.update;
            this.handler = ctx.handler;
            this.handlerArguments = ctx.handlerArguments;
            this.processor = ctx.processor;
            this.result = ctx.result;
            this.attributes.putAll(ctx.attributes);
        }

        public TelegramProcessContextBuilder cycle(ProcessCycle cycle) {
            this.cycle = cycle;
            return this;
        }

        public TelegramProcessContextBuilder update(Update update) {
            this.update = update;
            return this;
        }

        public TelegramProcessContextBuilder handler(TelegramHandler handler) {
            this.handler = handler;
            return this;
        }

        public TelegramProcessContextBuilder handlerArguments(Object[] handlerArguments) {
            this.handlerArguments = handlerArguments;
            return this;
        }

        public TelegramProcessContextBuilder processor(TelegramProcessor processor) {
            this.processor = processor;
            return this;
        }

        public TelegramProcessContextBuilder result(BotApiMethod<?> result) {
            this.result = result;
            return this;
        }

        public TelegramProcessContextBuilder attributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }
        public TelegramProcessContextBuilder addAttribute(String key, Object value) {
            this.attributes.put(key, value);
            return this;
        }
        public TelegramProcessContextBuilder removeAttribute(String key) {
            this.attributes.remove(key);
            return this;
        }

        public TelegramProcessContext build() {
            TelegramProcessContext ctx = new TelegramProcessContext();
            ctx.cycle = cycle;
            ctx.update = update;
            ctx.handler = handler;
            ctx.handlerArguments = handlerArguments;
            ctx.processor = processor;
            ctx.result = result;
            ctx.attributes.putAll(attributes);
            return ctx;
        }
    }

}
