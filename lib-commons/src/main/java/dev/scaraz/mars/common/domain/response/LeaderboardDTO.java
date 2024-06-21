package dev.scaraz.mars.common.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardDTO {

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


    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private long totalDurationResponse;
    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private int totalDivideResponse;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private long totalDurationAction;
    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private int totalDivideAction;


    public void incrementTotal() {
        total += 1;
    }

    public void incrementTotalDispatch() {
        totalDispatch += 1;
    }

    public void incrementTotalHandleDispatch() {
        totalHandleDispatch += 1;
    }

    public void sumTotalScore(double value) {
        this.totalScore += value;
    }

    public void increaseTotalActionDuration(long ms) {
        this.totalDivideAction += 1;
        this.totalDurationAction += ms;
    }

    public void increaseTotalResponseDuration(long ms) {
        this.totalDivideResponse += 1;
        this.totalDurationResponse += ms;
    }

}
