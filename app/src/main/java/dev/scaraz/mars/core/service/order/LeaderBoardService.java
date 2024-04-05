package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.response.LeaderBoardDTO;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.AuthorityConstant;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.Solution;
import dev.scaraz.mars.core.domain.view.LeaderBoardFragment;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.SolutionQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.criteria.LeaderBoardCriteria;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import dev.scaraz.mars.core.query.criteria.SolutionCriteria;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.query.spec.LeaderBoardSpecBuilder;
import dev.scaraz.mars.core.repository.db.view.LeaderBoardFragmentRepo;
import dev.scaraz.mars.core.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class LeaderBoardService {

    private final ConfigService configService;

    private final AccountQueryService accountQueryService;
    private final TicketQueryService ticketQueryService;

    private final LeaderBoardFragmentRepo repo;
    private final LeaderBoardSpecBuilder specBuilder;

    private final SolutionQueryService solutionQueryService;

    public List<LeaderBoardDTO> getLeaderboard(LeaderBoardCriteria criteria) {
        List<Account> accounts = accountQueryService.findAll(UserCriteria.builder()
                .roles(RoleCriteria.builder()
                        .name(new StringFilter().setEq(AuthorityConstant.AGENT_ROLE))
                        .build())
                .build(), Sort.by("name"));

        List<Long> solutionsId = configService.get(ConfigConstants.APP_SOLUTION_REPORT_EXCLUDE_LIST)
                .getAsLongList();

        if (solutionsId != null && !solutionsId.isEmpty()) {
            List<Solution> solutions = solutionQueryService.findAll(SolutionCriteria.builder()
                    .id(new LongFilter().setIn(solutionsId))
                    .build());

            criteria.setSolution(new SolutionCriteria()
                    .setId(new LongFilter().setNegated(true).setIn(solutions.stream()
                            .map(Solution::getId)
                            .toList()))
            );
        }

        return accounts.stream()
                .map(user -> this.getLeaderboard(user, criteria, false))
                .collect(Collectors.toList());
    }

    public List<LeaderBoardFragment> getLeaderboardFragments(LeaderBoardCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria), Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private LeaderBoardDTO getLeaderboard(Account user, LeaderBoardCriteria criteria, boolean includeFragmentAsResult) {
        criteria.setCreatedBy(new StringFilter().setEq(user.getNik()));
        List<LeaderBoardFragment> fragments = repo.findAll(specBuilder.createSpec(criteria));

        Duration avgAction = getAverageDuration(fragments, LeaderBoardFragment::getActionDuration);
        Duration avgResponse = getAverageDuration(fragments, LeaderBoardFragment::getResponseDuration);

        long totalHandleDispatch = fragments.stream()
                .filter(frg -> frg.getStart() == TcStatus.DISPATCH)
                .count();

        long totalDispatch = fragments.stream()
                .filter(frg -> frg.getClose() == TcStatus.DISPATCH)
                .count();

        Set<String> ticketIds = fragments.stream()
                .map(LeaderBoardFragment::getTicketId)
                .collect(Collectors.toSet());

        double totalScore = ticketQueryService.sumTotalScore(ticketIds);

        List<LeaderBoardFragment> includeFragments = includeFragmentAsResult ? fragments : null;
        return LeaderBoardDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .nik(user.getNik())
                .avgAction(avgAction)
                .avgResponse(avgResponse)
                .total(ticketIds.size())
                .totalScore(totalScore)
                .totalDispatch(totalDispatch)
                .totalHandleDispatch(totalHandleDispatch)
                .build();
    }

    private Duration getAverageDuration(Collection<LeaderBoardFragment> fragments, Function<LeaderBoardFragment, Duration> map) {
        List<Duration> list = fragments.stream().map(map).filter(Objects::nonNull).toList();
        if (list.isEmpty()) return Duration.ZERO;
        return Duration.ofMillis(list.stream()
                .map(Duration::toMillis)
                .reduce(Long::sum)
                .map(l -> l / list.size())
                .orElse(0L));
    }

}
