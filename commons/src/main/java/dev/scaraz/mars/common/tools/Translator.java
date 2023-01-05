package dev.scaraz.mars.common.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
@ConditionalOnClass(MessageSource.class)
public class Translator {
    public static final Locale
            LANG_EN = Locale.ENGLISH,
            LANG_ID = Locale.forLanguageTag("id-ID");
    private static volatile MessageSource instance;

    public Translator(ObjectProvider<MessageSource> source) {
        instance = source.getIfAvailable();
    }

    public static String tr(String code, Object... args) {
        return tr(code, LocaleContextHolder.getLocale(), args);
    }


    public static String tr(String code, Locale locale, Object... args) {
        if (instance == null) return code;
        return instance.getMessage(code, interceptArgs(args), locale);
    }

    @Nullable
    private static Object[] interceptArgs(Object... args) {
        return args.length == 0 ? null : args;
    }
}
