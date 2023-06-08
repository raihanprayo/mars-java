package dev.scaraz.mars.common.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderBoardDTO {

    private String id;

    private String nik;

    private String name;

    @Builder.Default
    private long avgRespon = 0;

    @Builder.Default
    private long avgAction = 0;

    @Builder.Default
    private int total = 0;

    @Builder.Default
    private int totalDispatch = 0;

    @Builder.Default
    private int totalHandleDispatch = 0;

    @Builder.Default
    private List<AgentWorklogDTO> worklogs = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof LeaderBoardDTO)) return false;

        LeaderBoardDTO that = (LeaderBoardDTO) o;

        return new EqualsBuilder()
                .append(getAvgRespon(), that.getAvgRespon())
                .append(getAvgAction(), that.getAvgAction())
                .append(getTotal(), that.getTotal())
                .append(getTotalDispatch(), that.getTotalDispatch())
                .append(getTotalHandleDispatch(), that.getTotalHandleDispatch())
                .append(getId(), that.getId())
                .append(getNik(), that.getNik())
                .append(getName(), that.getName())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getNik())
                .append(getName())
                .append(getAvgRespon())
                .append(getAvgAction())
                .append(getTotal())
                .append(getTotalDispatch())
                .append(getTotalHandleDispatch())
                .toHashCode();
    }
}
