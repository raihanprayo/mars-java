package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.type.InstantFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LeaderBoardCriteria implements Criteria {


    // User Criteria
    private StringFilter name;
    private StringFilter nik;

    // Leaderboard Criteria
    private StringFilter userId;
    private StringFilter ticketId;
    private StringFilter ticketNo;
    private InstantFilter createdAt;
    private InstantFilter updatedAt;

    private IssueCriteria issue;
    private SolutionCriteria solution;

}
