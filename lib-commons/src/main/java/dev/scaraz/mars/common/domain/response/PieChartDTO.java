package dev.scaraz.mars.common.domain.response;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PieChartDTO<T> {
    private T type;

    @Builder.Default
    private int value = 0;

    private String color;

}
