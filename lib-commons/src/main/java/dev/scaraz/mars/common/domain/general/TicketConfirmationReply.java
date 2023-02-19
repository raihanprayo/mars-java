package dev.scaraz.mars.common.domain.general;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketConfirmationReply {

    private int answer;

    @Getter
    private String no;

    public boolean isAgree() {
        return answer == 1;
    }
    public boolean isDisagree() {
        return answer == 0;
    }

    public static TicketConfirmationReply fromJson(String data) {
        return new Gson().fromJson(data, TicketConfirmationReply.class);
    }
}
