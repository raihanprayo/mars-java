package dev.scaraz.mars.common.domain.response;

import dev.scaraz.mars.common.domain.request.ForgotReqDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Duration;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotResDTO {

    private int length;

    private String token;

    private ForgotReqDTO.State next;

}
