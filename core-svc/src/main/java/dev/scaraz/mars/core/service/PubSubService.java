package dev.scaraz.mars.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Slf4j
@RequiredArgsConstructor

@Service
public class PubSubService {

    private static final Map<String, SseEmitter> EMITTERS = new TreeMap<>();

    private Optional<SseEmitter> get(String userId) {
        return Optional.ofNullable(EMITTERS.get(userId));
    }

    public SseEmitter subscribe(String userId) {
        SseEmitter sse = new SseEmitter();
        sse.onCompletion(() -> EMITTERS.remove(userId));
        sse.onError(ex -> EMITTERS.remove(userId));

        EMITTERS.put(userId, sse);
        try {
            sse.send("subscribed");
        }
        catch (IOException e) {
        }
        return sse;
    }

    public void sendToAll(SseEmitter.SseEventBuilder event) {
        log.info("NOTIFY TO ALL USER");
        for (String s : EMITTERS.keySet()) {
            SseEmitter sse = EMITTERS.get(s);
            try {
                sse.send(event);
            }
            catch (IOException e) {
                sse.completeWithError(e);
            }
        }
    }

    public void sendTo(String userId, SseEmitter.SseEventBuilder event) {
        get(userId)
                .ifPresent(sse -> {
                    try {
                        sse.send(event);
                    }
                    catch (IOException e) {
                        sse.completeWithError(e);
                    }
                });
    }

}
