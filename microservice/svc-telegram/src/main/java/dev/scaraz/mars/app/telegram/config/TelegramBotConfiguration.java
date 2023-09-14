package dev.scaraz.mars.app.telegram.config;

import dev.scaraz.mars.telegram.TelegramBotProperties;
import dev.scaraz.mars.telegram.TelegramProperties;
import dev.scaraz.mars.telegram.config.InternalTelegram;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.model.TelegramProcessContext;
import dev.scaraz.mars.telegram.service.LongPollingTelegramBotService;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.enums.ProcessCycle;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.CompletableFuture;

import static dev.scaraz.mars.telegram.util.TelegramUtil.TELEGRAM_EXECUTOR;

@Configuration
@RequiredArgsConstructor
public class TelegramBotConfiguration {

    private final TelegramProperties telegramProperties;

    @Bean(destroyMethod = "close")
    public TelegramBotService telegramBotService(
            TelegramBotsApi api,
            @Qualifier(TELEGRAM_EXECUTOR) TaskExecutor taskExecutor
    ) {
        return new InternalLongPollingBot(
                TelegramBotProperties.builder()
                        .token(telegramProperties.getToken())
                        .username(telegramProperties.getName())
                        .build(),
                api,
                taskExecutor
        );
    }

    private static class InternalLongPollingBot extends LongPollingTelegramBotService {

        public InternalLongPollingBot(TelegramBotProperties botProperties, TelegramBotsApi api, TaskExecutor executor) {
            super(botProperties, api, executor);
        }

        @Override
        protected TelegramLongPollingBot createBot(TelegramBotProperties botProperties) {
            return new TelegramLongPollingBot() {

                private final String token = botProperties.getToken();
                private final String username = botProperties.getUsername();

                @Override
                public String getBotToken() {
                    return this.token;
                }

                @Override
                public String getBotUsername() {
                    return this.username;
                }

                @Override
                public void onUpdateReceived(Update update) {
                    CompletableFuture.runAsync(() -> {
                        InternalLongPollingBot.this.onUpdateReceived(update);
                        try {
                            TelegramProcessContext ctx = TelegramContextHolder.get();
                            if (ctx.hasResult()) this.execute(ctx.getResult());
                        }
                        catch (TelegramApiException | IllegalStateException ex) {
                            InternalTelegram.update(c -> c.cycle(ProcessCycle.SEND));
                            TelegramContextHolder.getIfAvailable(ctx ->
                                    ctx.getProcessor().handleExceptions(InternalLongPollingBot.this, update, ex)
                            );
                        }
                        TelegramContextHolder.clear();
                    }, getExecutor());
                }
            };
        }
    }

}
