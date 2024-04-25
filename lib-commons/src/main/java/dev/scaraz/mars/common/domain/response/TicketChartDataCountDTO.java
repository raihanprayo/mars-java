package dev.scaraz.mars.common.domain.response;

import lombok.Data;

@Data
public class TicketChartDataCountDTO {
    private long total;
    private long internet;
    private long iptv;
    private long voice;
    private long others;
}
