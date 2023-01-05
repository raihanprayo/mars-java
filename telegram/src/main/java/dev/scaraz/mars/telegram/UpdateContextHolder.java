package dev.scaraz.mars.telegram;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public class UpdateContextHolder {
    private UpdateContextHolder() {}

    private static final ThreadLocal<Update> updateAttribute = new InheritableThreadLocal<>();

    public static void set(Update update) {
        updateAttribute.set(update);
    }

    public static void clear() {
        updateAttribute.remove();
    }

    public static Update get() {
        Update update = updateAttribute.get();
        if (update == null)
            throw new IllegalStateException("No Telegram Update bounded to this thread");

        return update;
    }

    public static boolean hasUpdate() {
        return Optional.ofNullable(updateAttribute.get())
                .isPresent();
    }

}
