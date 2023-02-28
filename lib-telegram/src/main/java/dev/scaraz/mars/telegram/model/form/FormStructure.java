package dev.scaraz.mars.telegram.model.form;


import com.fasterxml.jackson.annotation.JsonProperty;
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
public class FormStructure implements Serializable {

    @NotNull
    @JsonProperty("code")
    private String code;

    @Builder.Default
    @JsonProperty("waitInput")
    private boolean waitInput = false;

    @JsonProperty("field")
    private String fieldMap;

    @JsonProperty("sources")
    private List<FormSource> sources = new ArrayList<>();

    @Size(min = 1)
    @Builder.Default
    @JsonProperty("message")
    private List<String> message = new ArrayList<>();

}
