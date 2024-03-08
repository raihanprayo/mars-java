package dev.scaraz.mars.telegram.config;

import dev.scaraz.mars.telegram.model.form.FormStructure;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * TODO: Telegram Form Registry
 */
public class TelegramFormRegistry {

    private final Validator validator;
    private final List<FormStructure> forms;

    private final List<String> resourcePath = new ArrayList<>();

    public TelegramFormRegistry(List<FormStructure> container) {
        this.forms = container;
        this.validator = Validation.buildDefaultValidatorFactory()
                .getValidator();
    }

    public TelegramFormRegistry add(FormStructure structure) {
        Set<ConstraintViolation<FormStructure>> violations = validator.validate(structure);
        for (ConstraintViolation<FormStructure> violation : violations) {

        }
        forms.add(structure);
        return this;
    }

    public TelegramFormRegistry baseResource(String... paths) {
        return this;
    }

}
