package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.response.LeaderBoardDTO;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.AuthorityConstant;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.Issue;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.view.LeaderBoardFragment;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.criteria.LeaderBoardCriteria;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.query.spec.LeaderBoardSpecBuilder;
import dev.scaraz.mars.core.repository.db.view.LeaderBoardFragmentRepo;
import dev.scaraz.mars.core.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
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

    public List<LeaderBoardDTO> findAll(LeaderBoardCriteria criteria) {
        List<Account> accounts = accountQueryService.findAll(UserCriteria.builder()
                .name(criteria.getName())
                .nik(criteria.getNik())
                .roles(RoleCriteria.builder()
                        .name(new StringFilter().setEq(AuthorityConstant.AGENT_ROLE))
                        .build())
                .build());

        List<Long> solutionsId = configService.get(ConfigConstants.APP_SOLUTION_REPORT_EXCLUDE_LIST)
                .getAsLongList();

        if (solutionsId != null)
            criteria.setSolution(new LongFilter()
                    .setNegated(true)
                    .setIn(solutionsId));

        return accounts.stream()
                .map(this.getLeaderBoard(criteria))
                .collect(Collectors.toList());
    }

    private Function<Account, LeaderBoardDTO> getLeaderBoard(LeaderBoardCriteria criteria) {
        return user -> {
            criteria.setUserId(new StringFilter().setEq(user.getId()));
            List<LeaderBoardFragment> fragments = repo.findAll(specBuilder.createSpec(criteria));

            long avgAction = fragments.stream()
                    .map(LeaderBoardFragment::getActionDuration)
                    .map(Duration::toMillis)
                    .reduce(Long::sum)
                    .map(l -> l / fragments.size())
                    .orElse(0L);

            Set<String> ticketIds = fragments.stream()
                    .map(LeaderBoardFragment::getTicket)
                    .map(Ticket::getId)
                    .collect(Collectors.toSet());

            long totalHandleDispatch = fragments.stream()
                    .filter(frg -> frg.getStart() == TcStatus.DISPATCH)
                    .count();

            long totalDispatch = fragments.stream()
                    .filter(frg -> frg.getClose() == TcStatus.DISPATCH)
                    .count();

            long totalTickets = ticketIds.size();
            BigDecimal totalScore = ticketQueryService.sumAllTicketScoreByIds(ticketIds);

            return LeaderBoardDTO.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .nik(user.getNik())
                    .avgAction(avgAction)
                    .total(totalTickets)
                    .totalScore(totalScore)
                    .totalDispatch(totalDispatch)
                    .totalHandleDispatch(totalHandleDispatch)
                    .build();
        };
    }
}
