package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.domain.response.LeaderboardDTO;
import dev.scaraz.mars.common.domain.response.LeaderboardFragmentDTO;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.mapper.LeaderboardMapper;
import dev.scaraz.mars.core.query.LeaderboardQueryService;
import dev.scaraz.mars.core.query.criteria.LeaderboardCriteria;
import dev.scaraz.mars.core.service.order.LeaderBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
public class LeaderboardResource {

    private final LeaderboardMapper mapper;
    private final LeaderBoardService service;
    private final LeaderboardQueryService queryService;

    @GetMapping
    public ResponseEntity<?> getLeaderboard(LeaderboardCriteria criteria,
                                            Pageable pageable
    ) {
        List<LeaderboardDTO> page = service.leaderboardSummary(criteria);
        Optional<Sort.Order> first = pageable.getSort().get().findFirst();


        log.debug("Leaderboard sortBy - {}", first);
        log.debug("Leaderboard criteria - {}", criteria);
        if (first.isPresent()) {
            page = page.stream().sorted((a, b) -> {
                        Sort.Order order = first.get();
                        String property = order.getProperty();
                        Sort.Direction direction = order.getDirection();
                        switch (property) {
                            case "nik":
                                return direction == Sort.Direction.ASC ?
                                        a.getNik().compareTo(b.getNik()) :
                                        Collections.reverseOrder().compare(a.getNik(), b.getNik());
                            case "name":
                                return direction == Sort.Direction.ASC ?
                                        a.getName().compareTo(b.getName()) :
                                        Collections.reverseOrder().compare(a.getName(), b.getName());
                            case "avgAction":
//                                return direction == Sort.Direction.ASC ?
//                                         :
//                                        Duration.compare(b.getAvgAction(), a.getAvgAction());
                                return a.getAvgAction().compareTo(b.getAvgAction());
                            case "total":
                                return direction == Sort.Direction.ASC ?
                                        Long.compare(a.getTotal(), b.getTotal()) :
                                        Long.compare(b.getTotal(), a.getTotal());
                            case "totalDispatch":
                                return direction == Sort.Direction.ASC ?
                                        Long.compare(a.getTotalDispatch(), b.getTotalDispatch()) :
                                        Long.compare(b.getTotalDispatch(), a.getTotalDispatch());
                            case "totalHandleDispatch":
                                return direction == Sort.Direction.ASC ?
                                        Long.compare(a.getTotalHandleDispatch(), b.getTotalHandleDispatch()) :
                                        Long.compare(b.getTotalHandleDispatch(), a.getTotalHandleDispatch());
                            case "totalScore":
                                return direction == Sort.Direction.ASC ?
                                        Double.compare(a.getTotalScore(), b.getTotalScore()) :
                                        Double.compare(b.getTotalScore(), a.getTotalScore());
                            case "score": {
                                double a1 = a.getTotal() - (a.getTotalDispatch() * 0.1) + (a.getTotalHandleDispatch() * 0.1);
                                double b1 = b.getTotal() - (b.getTotalDispatch() * 0.1) + (b.getTotalHandleDispatch() * 0.1);
                                return direction == Sort.Direction.ASC ? Double.compare(a1, b1) : Double.compare(b1, a1);
                            }
                        }

                        return 0;
                    })
                    .collect(Collectors.toList());
        }
        else {
            page = page.stream().sorted(Comparator.comparing(LeaderboardDTO::getName))
                    .toList();
        }
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/fragment")
    public ResponseEntity<?> getLeaderboardFragments(LeaderboardCriteria criteria) {
        List<LeaderboardFragmentDTO> summaries = service.getFragments(criteria).stream()
                .map(mapper::toDTO)
                .toList();

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-total-count", String.valueOf(summaries.size()));
        return new ResponseEntity<>(summaries, headers, HttpStatus.OK);
    }


    @GetMapping("/download")
    public ResponseEntity<?> downloadRawLeadeboard(LeaderboardCriteria criteria) throws IOException {
        log.debug("Leaderboard criteria - {}", criteria);
        File file = service.exportToExcel(criteria);
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
        String ldt = "leaderboard_%s.xlsx".formatted(LocalDateTime.now().format(formatDate));
        return ResourceUtil.download(file, ldt);
    }

//    @GetMapping("/detail/{nik}")
//    public ResponseEntity<?> getLeaderboardRawData(@PathVariable String nik,
//                                                   LeaderBoardCriteria criteria) {
//        criteria.setCreatedBy(new StringFilter().setEq(nik));
//        criteria.setUpdatedBy(new StringFilter().setOpt(Filter.Opt.OR).setEq(nik));
//
//        List<LeaderBoardFragment> fragments = leaderBoardService.getLeaderboardFragments(criteria);
//        return ResponseEntity.ok(fragments.stream()
//                .map(mapper::toDTO)
//                .toList());
//    }

}
