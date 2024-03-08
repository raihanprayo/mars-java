package dev.scaraz.mars.telegram.model.form;

import lombok.*;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CallbackQueryFormSource extends FormSource {

    @NotNull
    @Min(1)
    private String pattern;

}
