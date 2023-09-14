package dev.scaraz.mars.telegram.service;

import dev.scaraz.mars.telegram.TelegramBotProperties;
import dev.scaraz.mars.telegram.config.InternalTelegram;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.model.TelegramProcessContext;
import dev.scaraz.mars.telegram.util.enums.ProcessCycle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;

import java.util.concurrent.*;

/**
 * Long polling implementation of Telegram Bot Service.
 *
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Slf4j
public class LongPollingTelegramBotService extends TelegramBotService {
//    private final ExecutorService botExecutor;

    private final TelegramBotsApi api;
    private final TelegramLongPollingBot client;

    @Getter
    private final TaskExecutor executor;

    @Getter
    private BotSession session;

    public LongPollingTelegramBotService(TelegramBotProperties botProperties,
                                         TelegramBotsApi api,
                                         TaskExecutor executor
    ) {
        log.info("Registering Long Polling with {}", botProperties);

        this.api = api;
        this.client = createBot(botProperties);
        this.executor = executor;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() throws TelegramApiException {
        log.info("Longpolling Bot Starting");
        this.session = api.registerBot(client);
    }

    @Override
    public TelegramLongPollingBot getClient() {
        return client;
    }

    @Override
    public void close() {
//        if (session == null) return;
//
//        if (session.isRunning()) {
//            log.info("Shutting down bot session");
//            session.stop();
//        }
    }

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
                    LongPollingTelegramBotService.this.onUpdateReceived(update);
                    try {
                        TelegramProcessContext ctx = TelegramContextHolder.get();
                        if (ctx.hasResult()) this.execute(ctx.getResult());
                    }
                    catch (TelegramApiException | IllegalStateException ex) {
                        InternalTelegram.update(c -> c.cycle(ProcessCycle.SEND));
                        TelegramContextHolder.getIfAvailable(ctx ->
                                ctx.getProcessor().handleExceptions(LongPollingTelegramBotService.this, update, ex)
                        );
                    }
                    TelegramContextHolder.clear();
                }, LongPollingTelegramBotService.this.executor);
            }
        };
    }

}
