package dev.scaraz.mars.telegram.service;

import dev.scaraz.mars.telegram.TelegramBotProperties;
import dev.scaraz.mars.telegram.config.InternalTelegram;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.model.TelegramProcessContext;
import dev.scaraz.mars.telegram.util.enums.ProcessCycle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
public class LongPollingTelegramBotService extends TelegramBotService implements AutoCloseable {
    private final ExecutorService botExecutor;

    private final TelegramLongPollingBot client;

    @Getter
    private final BotSession session;

    private final TaskExecutor executor;

    public LongPollingTelegramBotService(TelegramBotProperties botProperties,
                                         TelegramBotsApi api, TaskExecutor executor) {
        log.info("Registering Long Polling with {}", botProperties);

        this.client = createBot(botProperties);
        this.executor = executor;
        try {
            this.session = api.registerBot(client);
            this.botExecutor = new ThreadPoolExecutor(1, botProperties.getMaxThreads(),
                    1L, TimeUnit.HOURS,
                    new SynchronousQueue<>()
            );
        }
        catch (TelegramApiException e) {
            log.error("Cannot register Long Polling with {}", botProperties, e);
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
        }
        catch (InterruptedException e) {
            log.error("Bot executor service termination awaiting failed", e);
        }

        if (!terminated) {
            int droppedTasks = botExecutor.shutdownNow().size();
            log.error("Executor was abruptly shut down. {} tasks will not be executed", droppedTasks);
        }
    }

    private TelegramLongPollingBot createBot(TelegramBotProperties botProperties) {
        LongPollingTelegramBotService self = this;
        return new TelegramLongPollingBot() {
            @Override
            public String getBotToken() {
                return botProperties.getToken();
            }

            @Override
            public String getBotUsername() {
                return botProperties.getUsername();
            }

            @Override
            public void onUpdateReceived(Update update) {
                CompletableFuture.runAsync(() -> {

                    self.onUpdateReceived(update);
                    try {
                        TelegramProcessContext ctx = TelegramContextHolder.get();
                        if (ctx.hasResult()) this.execute(ctx.getResult());
                    }
                    catch (TelegramApiException | IllegalStateException ex) {
                        InternalTelegram.update(c -> c.cycle(ProcessCycle.SEND));
                        TelegramContextHolder.getIfAvailable(ctx ->
                                ctx.getProcessor().handleExceptions(self, update, ex)
                        );
                    }

                    TelegramContextHolder.clear();
                }, self.executor);
            }
        };
    }

}
