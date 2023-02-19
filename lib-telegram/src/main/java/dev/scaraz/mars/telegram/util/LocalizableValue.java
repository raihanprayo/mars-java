package dev.scaraz.mars.telegram.util;

/**
 * Localizable value.
 *
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public interface LocalizableValue {
    /**
     * @return code in resource bundle
     */
    String getTranslationTag();
}
