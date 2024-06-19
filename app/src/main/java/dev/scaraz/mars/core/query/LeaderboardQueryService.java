package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.agent.Leaderboard;
import dev.scaraz.mars.core.query.criteria.LeaderboardCriteria;
import dev.scaraz.mars.core.query.spec.WorkSummarySpecBuilder;
import dev.scaraz.mars.core.repository.db.agent.WorkSummaryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaderboardQueryService {

    private final WorkSummaryRepo repo;
    private final WorkSummarySpecBuilder specBuilder;


    public Page<Leaderboard> findAll(LeaderboardCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }


    public List<Leaderboard> findAll(LeaderboardCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    public long count(LeaderboardCriteria criteria) {
        return repo.count(specBuilder.createSpec(criteria));
    }

}
