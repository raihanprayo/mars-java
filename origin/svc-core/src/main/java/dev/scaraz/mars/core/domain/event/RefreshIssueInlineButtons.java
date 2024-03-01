package dev.scaraz.mars.core.domain.event;

import org.springframework.context.ApplicationEvent;

public class RefreshIssueInlineButtons extends ApplicationEvent {
    public RefreshIssueInlineButtons(String source) {
        super(source);
    }
    public RefreshIssueInlineButtons() {
        this("refresh");
    }

    @Override
    public String getSource() {
        return (String) super.getSource();
    }
}
