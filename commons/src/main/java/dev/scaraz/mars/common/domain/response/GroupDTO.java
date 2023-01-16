package dev.scaraz.mars.common.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {

    private String id;

    private String name;

    @Builder.Default
    private GroupSettingDTO setting = new GroupSettingDTO();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupSettingDTO {
        private boolean canLogin;
    }
}
