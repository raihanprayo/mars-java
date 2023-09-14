package dev.scaraz.mars.common.tools.enums;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Witel {
    ROC,
    BANTEN,
    BEKASI,
    BOGOR,
    JAKBAR,
    JAKPUS,
    JAKSEL,
    JAKTIM,
    JAKUT,
    TANGERANG;

    public String callbackData() {
        return "WITEL_" + name();
    }

    public static final String[] CALLBACK_DATAS = Arrays.stream(values())
            .map(Witel::callbackData)
            .toArray(String[]::new);

    public static List<List<InlineKeyboardButton>> generateKeyboardButtons() {
        Witel[] values = Witel.values();
        List<List<InlineKeyboardButton>> keyboards = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int i = 0; i < values.length; i++) {
            Witel witel = values[i];

            if (i % 3 == 0) {
                keyboards.add(row);
                row = new ArrayList<>();
            }

            String text = witel.name();
            text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
            row.add(InlineKeyboardButton.builder()
                    .text(text)
                    .callbackData(witel.callbackData())
                    .build());
        }

        return keyboards;
    }


    public static Witel fromCallbackData(String callbackData) throws IllegalArgumentException {
        String[] split = callbackData.split("_");
        if (split.length != 2) throw new IllegalArgumentException("Invalid Witel callback data");
        return valueOf(split[1].toUpperCase());
    }

}
