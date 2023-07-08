package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.domain.response.PieChartDTO;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.service.order.ChartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@RequiredArgsConstructor

@Service
public class ChartServiceImpl implements ChartService {

//    @Override
//    @Async
//    @Transactional(readOnly = true)
//    public CompletableFuture<Page<UserLeaderboardDTO>> getLeaderBoard(
//            LeaderboardCriteria criteria,
//            Pageable pageable
//    ) {
//
//        Page<User> users = userQueryService.findAll(UserCriteria.builder()
//                .roles(RoleCriteria.builder()
//                        .name(new StringFilter()
//                                .setEq(AppConstants.Authority.AGENT_ROLE))
//                        .build())
//                .build(), pageable);
//
//        Map<String, UserLeaderboardDTO> leaderboards = new HashMap<>();
//
//        TicketCriteria dispatchesTicketCriteria = TicketCriteria.builder()
//                .product(criteria.getProduct())
//                .build();
//        AgentCriteria totalDispatchCriteria = AgentCriteria.builder()
//                .closeStatus(new TcStatusFilter().setEq(TcStatus.DISPATCH))
//                .userId(new StringFilter())
//                .ticket(dispatchesTicketCriteria)
//                .createdAt(criteria.getRange())
//                .build();
//        AgentCriteria totalHandleDispatchCriteria = AgentCriteria.builder()
//                .takeStatus(new TcStatusFilter().setEq(TcStatus.DISPATCH))
//                .userId(new StringFilter())
//                .ticket(dispatchesTicketCriteria)
//                .createdAt(criteria.getRange())
//                .build();
//        TicketCriteria totalCriteria = TicketCriteria.builder()
//                .agents(AgentCriteria.builder()
//                        .userId(new StringFilter())
//                        .build())
//                .product(criteria.getProduct())
//                .createdAt(criteria.getRange())
//                .build();
//
//        for (User user : users) {
//            UserLeaderboardDTO leaderboard = UserLeaderboardDTO.builder()
//                    .id(user.getId())
//                    .nik(user.getNik())
//                    .name(user.getName())
//                    .build();
//            leaderboards.put(user.getId(), leaderboard);
//
//            totalDispatchCriteria.getUserId().setEq(user.getId());
//            totalHandleDispatchCriteria.getUserId().setEq(user.getId());
//            totalCriteria.getAgents().getUserId().setEq(user.getId());
//
//            leaderboard.setTotalDispatch((int) agentQueryService.count(totalDispatchCriteria));
//            leaderboard.setTotalHandleDispatch((int) agentQueryService.count(totalHandleDispatchCriteria));
//
//            List<Ticket> tickets = ticketQueryService.findAll(totalCriteria);
//            leaderboard.setTotal(tickets.size());
//
//            leaderBoardResponseTime(user.getId(), leaderboard, criteria);
//        }
//
//        return CompletableFuture.completedFuture(users
//                .map(User::getId)
//                .map(leaderboards::get)
//        );
//    }

    @Override
    @Transactional(readOnly = true)
    public List<PieChartDTO<String>> pieTicketByAge(List<TicketSummary> summaries) {
        Map<String, PieChartDTO<String>> category = createCategoryMap();
        Instant now = Instant.now();

        for (TicketSummary summary : summaries)
            groupAndPush(now, summary.getCreatedAt(), category);

        return new ArrayList<>(category.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PieChartDTO<String>> pieTicketByActionAge(List<TicketSummary> summaries) {
        Map<String, PieChartDTO<String>> category = createCategoryMap();
        Instant now = Instant.now();

        for (TicketSummary summary : summaries) {
            groupAndPush(now, summary.getAge().getAction(), category);
        }

        return new ArrayList<>(category.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PieChartDTO<String>> pieTicketByResponseAge(List<TicketSummary> summaries) {
        Map<String, PieChartDTO<String>> category = createCategoryMap();
        for (TicketSummary summary : summaries) {
            groupAndPush(summary.getCreatedAt(), summary.getAge().getResponse(), category);
        }

        return new ArrayList<>(category.values());
    }

    @Override
    public Instant[] rangeConvert(LocalDate from, LocalDate to) {
        Instant startInst = from.atStartOfDay().toInstant(ZoneOffset.of("+07"));
        Instant endInst = to.atStartOfDay()
                .plusDays(1)
                .minusSeconds(1)
                .toInstant(ZoneOffset.of("+07"));
        return new Instant[]{startInst, endInst};
    }

    private Map<String, PieChartDTO<String>> createCategoryMap() {
        Map<String, PieChartDTO<String>> category = new TreeMap<>();
        category.put(CATEGORY_15_MINUTES, PieChartDTO.<String>builder()
                .type(CATEGORY_15_MINUTES)
                .value(0)
                .color("blue")
                .build());

        category.put(CATEGORY_30_MINUTES, PieChartDTO.<String>builder()
                .type(CATEGORY_30_MINUTES)
                .value(0)
                .color("orange")
                .build());

        category.put(CATEGORY_60_MINUTES, PieChartDTO.<String>builder()
                .type(CATEGORY_60_MINUTES)
                .value(0)
                .color("purple")
                .build());

        category.put(CATEGORY_MORE_THAN_60_MINUTES, PieChartDTO.<String>builder()
                .type(CATEGORY_MORE_THAN_60_MINUTES)
                .value(0)
                .color("red")
                .build());
        return category;
    }

    private void groupAndPush(Instant now, @Nullable Instant dataCreatedAt, Map<String, PieChartDTO<String>> category) {
        long durationMili = now.toEpochMilli() - Optional.ofNullable(dataCreatedAt)
                .orElse(now)
                .toEpochMilli();

        if (durationMili <= MILI_15_MINUTES.toMillis()) {
            category.computeIfPresent(CATEGORY_15_MINUTES, (k, v) -> {
                v.setValue(v.getValue() + 1);
                return v;
            });
        }
        else if (durationMili <= MILI_30_MINUTES.toMillis()) {
            category.computeIfPresent(CATEGORY_30_MINUTES, (k, v) -> {
                v.setValue(v.getValue() + 1);
                return v;
            });
        }
        else if (durationMili <= MILI_60_MINUTES.toMillis()) {
            category.computeIfPresent(CATEGORY_60_MINUTES, (k, v) -> {
                v.setValue(v.getValue() + 1);
                return v;
            });
        }
        else {
            category.computeIfPresent(CATEGORY_MORE_THAN_60_MINUTES, (k, v) -> {
                v.setValue(v.getValue() + 1);
                return v;
            });
        }
    }

//    private void leaderBoardResponseTime(String userId, UserLeaderboardDTO leaderboard, LeaderboardCriteria criteria) {
//        List<Agent> agents = agentQueryService.findAll(AgentCriteria.builder()
//                .userId(new StringFilter().setEq(userId))
//                .takeStatus(new TcStatusFilter().setIn(List.of(TcStatus.OPEN, TcStatus.REOPEN, TcStatus.DISPATCH)))
//                .ticket(TicketCriteria.builder()
//                        .product(criteria.getProduct())
//                        .build())
//                .createdAt(criteria.getRange())
//                .build());
//
//        List<Long> avgRespon = new ArrayList<>();
//        List<Long> avgAction = new ArrayList<>();
//
//        for (Agent agent : agents) {
//            if (agent.getTakeStatus() == TcStatus.OPEN) {
//                long responMilis = agent.getCreatedAt().toEpochMilli() - agent.getTicket().getCreatedAt().toEpochMilli();
//                avgRespon.add(responMilis);
//            }
//
//            if (agent.getStatus() == AgStatus.CLOSED) {
//                long actionMilis = agent.getUpdatedAt().toEpochMilli() - agent.getCreatedAt().toEpochMilli();
//                avgAction.add(actionMilis);
//            }
//        }
//
//        if (!avgRespon.isEmpty())
//            leaderboard.setAvgResponTime(avgRespon.stream()
//                    .reduce(Long::sum)
//                    .orElse(0L) / avgRespon.size());
//
//        if (!avgAction.isEmpty())
//            leaderboard.setAvgActionTime(avgAction.stream()
//                    .reduce(Long::sum)
//                    .orElse(0L) / avgAction.size());
//    }
}
