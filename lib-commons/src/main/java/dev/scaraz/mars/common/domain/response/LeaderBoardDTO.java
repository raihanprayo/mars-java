package dev.scaraz.mars.common.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderBoardDTO {

    private String id;

    private String nik;

    private String name;

    @Builder.Default
    private long avgAction = 0;

    @Builder.Default
    private long total = 0;

    @Builder.Default
    private BigDecimal totalScore = BigDecimal.ZERO;

    @Builder.Default
    private long totalDispatch = 0;

    @Builder.Default
    private long totalHandleDispatch = 0;

}
