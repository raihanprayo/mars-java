package dev.scaraz.mars.telegram.model.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class FormSource implements Serializable {

    @JsonProperty("type")
    private HandlerType type;

}
