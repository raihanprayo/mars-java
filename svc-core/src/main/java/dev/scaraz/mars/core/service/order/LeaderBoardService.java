package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.response.AgentWorklogDTO;
import dev.scaraz.mars.common.domain.response.LeaderBoardDTO;
import dev.scaraz.mars.common.domain.response.TicketShortDTO;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.AgentWorklog;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.view.LeaderBoardFragment;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.mapper.AgentMapper;
import dev.scaraz.mars.core.mapper.TicketMapper;
import dev.scaraz.mars.core.query.AgentWorklogQueryService;
import dev.scaraz.mars.core.query.AgentWorkspaceQueryService;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.query.criteria.*;
import dev.scaraz.mars.core.query.spec.LeaderBoardSpecBuilder;
import dev.scaraz.mars.core.repository.view.LeaderBoardFragmentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class LeaderBoardService {

    private final UserQueryService userQueryService;

    private final LeaderBoardFragmentRepo repo;
    private final LeaderBoardSpecBuilder specBuilder;

//    private final AgentMapper agentMapper;
//    private final AgentWorklogQueryService agentWorklogQueryService;
//    private final AgentWorkspaceQueryService agentWorkspaceQueryService;
//
//    private final TicketMapper ticketMapper;
//    private final TicketSummaryQueryService ticketSummaryQueryService;

    public List<LeaderBoardDTO> findAll(LeaderBoardCriteria criteria) {
        List<User> users = userQueryService.findAll(UserCriteria.builder()
                .name(criteria.getName())
                .nik(criteria.getNik())
                .roles(RoleCriteria.builder()
                        .name(new StringFilter().setEq(AppConstants.Authority.AGENT_ROLE))
                        .build())
                .build());

        return users.stream()
                .map(this.getLeaderBoard(criteria))
                .collect(Collectors.toList());
    }

    private Function<User, LeaderBoardDTO> getLeaderBoard(LeaderBoardCriteria criteria) {
        return user -> {
            criteria.setUserId(new StringFilter().setEq(user.getId()));
            List<LeaderBoardFragment> fragments = repo.findAll(specBuilder.createSpec(criteria));

            long avgAction = fragments.stream()
                    .map(LeaderBoardFragment::getActionDuration)
                    .map(Duration::toMillis)
                    .reduce(Long::sum)
                    .orElse(0L) / fragments.size();

            long totalHandleDispatch = fragments.stream()
                    .filter(frg -> frg.getStart() == TcStatus.DISPATCH)
                    .count();

            long totalDispatch = fragments.stream()
                    .filter(frg -> frg.getClose() == TcStatus.DISPATCH)
                    .count();

            long totalTickets = fragments.stream()
                    .map(LeaderBoardFragment::getTicket)
                    .map(Ticket::getId)
                    .collect(Collectors.toSet())
                    .size();

            return LeaderBoardDTO.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .nik(user.getNik())
                    .avgAction(avgAction)
                    .total(totalTickets)
                    .totalDispatch(totalDispatch)
                    .totalHandleDispatch(totalHandleDispatch)
                    .build();
        };
    }
}