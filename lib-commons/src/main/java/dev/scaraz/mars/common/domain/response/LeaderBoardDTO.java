package dev.scaraz.mars.common.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderBoardDTO {

    private String id;

    private String nik;

    private String name;

    @Builder.Default
    private Duration avgAction = Duration.ZERO;

    @Builder.Default
    private Duration avgResponse = Duration.ZERO;

    @Builder.Default
    private long total = 0;

    @Builder.Default
    private double totalScore = 0.0;

    @Builder.Default
    private long totalDispatch = 0;

    @Builder.Default
    private long totalHandleDispatch = 0;

    private List<LeaderBoardFragmentDTO> fragments;

}
