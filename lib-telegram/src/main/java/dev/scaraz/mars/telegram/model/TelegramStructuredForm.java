package dev.scaraz.mars.telegram.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.scaraz.mars.telegram.model.form.FormStructure;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramStructuredForm implements Serializable {

    @NotNull
    @JsonProperty("form")
    private String form;

    @JsonProperty("entry")
    private String entry = "START";

    @Size(min = 1)
    @JsonProperty("guides")
    private List<FormStructure> guides = new ArrayList<>();

    @NotNull
    @JsonProperty("step")
    private List<String> step = new ArrayList<>();

}
