package dev.scaraz.mars.common.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLeaderboardDTO {

    private String id;
    private String nik;
    private String name;



    private int totalDispatch;

    private int totalHandleDispatch;

    private int total;

    @Builder.Default
    private long avgResponTime = 0;

    @Builder.Default
    private long avgActionTime = 0;

}
