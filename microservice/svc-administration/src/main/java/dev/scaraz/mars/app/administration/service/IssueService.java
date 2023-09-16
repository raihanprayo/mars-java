package dev.scaraz.mars.app.administration.service;

import dev.scaraz.mars.app.administration.domain.db.Config;
import dev.scaraz.mars.app.administration.domain.extern.Issue;
import dev.scaraz.mars.app.administration.repository.extern.IssueRepo;
import dev.scaraz.mars.app.administration.service.app.ConfigService;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.utils.AppConstants;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepo repo;
    private final ConfigService configService;

    public List<List<InlineKeyboardButton>> getKeyboards(Witel witel) {
        List<Witel> witels = new ArrayList<>();
        witels.add(null);
        witels.add(witel);
        List<Issue> issues = repo.findAllByDeletedIsFalseAndWitelIn(witels);
        int rowCount = configService.get(witel, Config.TG_CMD_ISSUE_COLUMN_INT)
                .getAsInt();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int i = 0; i < issues.size(); i++) {
            Issue issue = issues.get(i);
            if (i % rowCount == 0) {
                buttons.add(row);
                row = new ArrayList<>();
            }

            String display = StringUtils.isBlank(issue.getName()) ?
                    issue.getCode() : issue.getName();

            row.add(InlineKeyboardButton.builder()
                    .text(display)
                    .callbackData(AppConstants.Telegram.REPORT_ISSUE + issue.getCode())
                    .build());
        }

        return buttons;
    }

}
