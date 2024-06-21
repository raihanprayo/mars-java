package dev.scaraz.mars.core.mapper;

import dev.scaraz.mars.common.domain.response.LeaderboardFragmentDTO;
import dev.scaraz.mars.core.domain.agent.Leaderboard;
import dev.scaraz.mars.core.service.order.LeaderBoardService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

@Mapper
public abstract class LeaderboardMapper {

    @Autowired
    protected LeaderBoardService service;

    @Mapping(target = "scoreAction", source = "durationAction", qualifiedByName = "get-action-score")
    @Mapping(target = "scoreResponse", source = "durationResponse", qualifiedByName = "get-response-score")
    public abstract LeaderboardFragmentDTO toDTO(Leaderboard lb);

    @Named("get-action-score")
    public double getActionScore(Duration interval) {
        return service.scoreAction(interval);
    }

    @Named("get-response-score")
    public double getResponseScore(Duration interval) {
        return service.scoreResponse(interval);
    }

}
