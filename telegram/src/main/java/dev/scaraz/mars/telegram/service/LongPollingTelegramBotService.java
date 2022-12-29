package dev.scaraz.mars.telegram.service;

import dev.scaraz.mars.telegram.TelegramBotProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Long polling implementation of Telegram Bot Service.
 *
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Slf4j
public class LongPollingTelegramBotService extends TelegramBotService implements AutoCloseable {

    private final String username;
    private final String token;
    private final ExecutorService botExecutor;
    private final TelegramLongPollingBot client;

    public LongPollingTelegramBotService(TelegramBotProperties botBuilder,
                                         TelegramBotsApi api,
                                         EmbeddedValueResolver valueResolver) {
        super(valueResolver);
        log.info("Registering Long Polling with {}", botBuilder);
        username = botBuilder.getUsername();
        token = botBuilder.getToken();
        botExecutor = new ThreadPoolExecutor(1, botBuilder.getMaxThreads(),
            1L, TimeUnit.HOURS,
            new SynchronousQueue<>()
        );

        client = new TelegramBotLongPollingImpl();
        try {
            api.registerBot(client);
        } catch (TelegramApiException e) {
            log.error("Cannot register Long Polling with {}", botBuilder, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public TelegramLongPollingBot getClient() {
        return client;
    }

    @Override
    public void close() {
        botExecutor.shutdown();
        boolean terminated = false;
        try {
            terminated = botExecutor.awaitTermination(5, TimeUnit.SECONDS);
            if (!terminated) {
                log.error("Bot executor did not terminated in 5 seconds");
            }
        } catch (InterruptedException e) {
            log.error("Bot executor service termination awaiting failed", e);
        }

        if (!terminated) {
            int droppedTasks = botExecutor.shutdownNow().size();
            log.error("Executor was abruptly shut down. {} tasks will not be executed", droppedTasks);
        }
    }

    private class TelegramBotLongPollingImpl extends TelegramLongPollingBot {
        @Override
        public void onUpdateReceived(Update update) {
            CompletableFuture.runAsync(() ->
                updateProcess(update).ifPresent(result -> {
                    try {
                        getClient().execute(result);
                        log.debug("Update: {}. Message: {}. Successfully sent", update, result);
                    } catch (TelegramApiException e) {
                        log.error("Update: {}. Cannot send message {} to telegram: ", update.getUpdateId(), result, e);
                    }
                }), botExecutor);
        }

        @Override
        public String getBotUsername() {
            return username;
        }

        @Override
        public String getBotToken() {
            return token;
        }
    }
}
