package dev.scaraz.mars.common.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketPieChartDTO {

    @Builder.Default
    private List<PieChartDTO<String>> age = new ArrayList<>();

    @Builder.Default
    private List<PieChartDTO<String>> actionAge = new ArrayList<>();

    @Builder.Default
    private List<PieChartDTO<String>> responseAge = new ArrayList<>();

    @Builder.Default
    private TicketChartDataCountDTO count = new TicketChartDataCountDTO();

    @Data
    public static class TicketChartDataCountDTO {
        private long total;
        private long internet;
        private long iptv;
        private long voice;
    }
}
