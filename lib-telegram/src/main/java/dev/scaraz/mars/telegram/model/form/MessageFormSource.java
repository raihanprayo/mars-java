package dev.scaraz.mars.telegram.model.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nullable;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MessageFormSource extends FormSource {

    @Builder.Default
    @JsonProperty("fromReply")
    private boolean fromReply = false;

    @Nullable
    @Pattern(regexp = "/[\\w_]+")
    @JsonProperty("command")
    private String command;

}
