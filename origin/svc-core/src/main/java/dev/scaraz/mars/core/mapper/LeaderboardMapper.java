package dev.scaraz.mars.core.mapper;

import dev.scaraz.mars.common.domain.response.LeaderBoardFragmentDTO;
import dev.scaraz.mars.core.domain.view.LeaderBoardFragment;
import org.mapstruct.Mapper;

@Mapper(uses = {SolutionMapper.class, IssueMapper.class})
public abstract class LeaderboardMapper {

    public abstract LeaderBoardFragmentDTO toDTO(LeaderBoardFragment fragment);

}
