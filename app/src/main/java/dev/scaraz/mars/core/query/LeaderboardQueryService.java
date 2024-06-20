package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.agent.Leaderboard;
import dev.scaraz.mars.core.query.criteria.LeaderboardCriteria;
import dev.scaraz.mars.core.query.spec.LeaderboardSpecBuilder;
import dev.scaraz.mars.core.repository.db.agent.LeaderboardRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaderboardQueryService {

    private final LeaderboardRepo repo;
    private final LeaderboardSpecBuilder specBuilder;


    public Page<Leaderboard> findAll(LeaderboardCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }


    public List<Leaderboard> findAll(LeaderboardCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }


    public List<Leaderboard> findAll(LeaderboardCriteria criteria, Sort sort) {
        return repo.findAll(specBuilder.createSpec(criteria), sort);
    }

    public long count(LeaderboardCriteria criteria) {
        return repo.count(specBuilder.createSpec(criteria));
    }

}
