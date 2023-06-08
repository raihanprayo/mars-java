package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.response.AgentWorklogDTO;
import dev.scaraz.mars.common.domain.response.LeaderBoardDTO;
import dev.scaraz.mars.common.domain.response.TicketShortDTO;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.AgentWorklog;
import dev.scaraz.mars.core.domain.view.LeaderBoardFragment;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.mapper.AgentMapper;
import dev.scaraz.mars.core.mapper.TicketMapper;
import dev.scaraz.mars.core.query.AgentWorklogQueryService;
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

import java.util.ArrayList;
import java.util.List;
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

    private final AgentMapper agentMapper;
    private final AgentWorklogQueryService agentWorklogQueryService;

    private final TicketMapper ticketMapper;
    private final TicketSummaryQueryService ticketSummaryQueryService;

    public Page<LeaderBoardDTO> findAll(LeaderBoardCriteria criteria, Pageable pageable) {
        Page<User> users = userQueryService.findAll(UserCriteria.builder()
                .name(criteria.getName())
                .nik(criteria.getNik())
                .roles(RoleCriteria.builder()
                        .name(new StringFilter().setEq(AppConstants.Authority.AGENT_ROLE))
                        .build())
                .build(), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));

        return users.map(this.getLeaderBoard(criteria));
    }

    private Function<User, LeaderBoardDTO> getLeaderBoard(LeaderBoardCriteria criteria) {
        return user -> {
            List<LeaderBoardFragment> fragments = repo.findAll(
                    specBuilder.createSpec(criteria.toBuilder()
                            .userId(new StringFilter().setEq(user.getId()))
                            .build()));

            long avgRespon = fragments.stream()
                    .map(LeaderBoardFragment::getAvgRespon)
                    .reduce(0L, Long::sum);
            long avgAction = fragments.stream()
                    .map(LeaderBoardFragment::getAvgAction)
                    .reduce(0L, Long::sum);

            int total = (int) fragments.stream()
                    .map(LeaderBoardFragment::getTicketNo)
                    .distinct()
                    .count();

            int totalDispatch = fragments.stream()
                    .map(LeaderBoardFragment::getTotalDispatch)
                    .reduce(0, Integer::sum);
            int totalHandleDispatch = fragments.stream()
                    .map(LeaderBoardFragment::getTotalHandleDispatch)
                    .reduce(0, Integer::sum);

//            List<Long> ids = fragments.stream().map(LeaderBoardFragment::getId)
//                    .collect(Collectors.toList());
//
//            Page<AgentWorklog> worklogs = agentWorklogQueryService.findAll(
//                            AgentWorklogCriteria.builder()
//                                    .workspace(AgentWorkspaceCriteria.builder()
//                                            .id(new LongFilter().setIn(ids))
//                                            .build())
//                                    .build(),
//                            PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.ASC, "createdAt")
//                    );

            List<AgentWorklogDTO> worklogs = new ArrayList<>();
            for (LeaderBoardFragment frg : fragments) {
                TicketShortDTO ticket = ticketMapper.toShortDTO(ticketSummaryQueryService.findByIdOrNo(frg.getTicketNo()));
                List<AgentWorklog> worklogsRaw = agentWorklogQueryService.findAll(AgentWorklogCriteria.builder()
                        .workspace(AgentWorkspaceCriteria.builder()
                                .id(new LongFilter().setEq(frg.getId()))
                                .build())
                        .build());

                worklogsRaw.stream()
                        .map(agentMapper::toDTO)
                        .peek(dto -> dto.setTicket(ticket))
                        .forEach(worklogs::add);
            }

            return LeaderBoardDTO.builder()
                    .id(user.getId())
                    .nik(user.getNik())
                    .name(user.getName())
                    .avgAction(avgAction)
                    .avgRespon(avgRespon)
                    .total(total)
                    .totalDispatch(totalDispatch)
                    .totalHandleDispatch(totalHandleDispatch)
                    .worklogs(worklogs)
                    .build();
        };
    }
}
