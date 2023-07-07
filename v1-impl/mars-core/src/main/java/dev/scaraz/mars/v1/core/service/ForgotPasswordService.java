package dev.scaraz.mars.v1.core.service;

import dev.scaraz.mars.common.domain.request.ForgotReqDTO;
import dev.scaraz.mars.common.domain.response.JwtResult;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.v1.core.config.datasource.AuditProvider;
import dev.scaraz.mars.v1.core.domain.cache.ForgotPassword;
import dev.scaraz.mars.v1.core.domain.credential.User;
import dev.scaraz.mars.v1.core.query.UserQueryService;
import dev.scaraz.mars.v1.core.repository.cache.ForgotPasswordRepo;
import dev.scaraz.mars.v1.core.service.credential.UserService;
import dev.scaraz.mars.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private static final String NUMBERS = "0123456789";

    private final ForgotPasswordRepo repo;

    private final AuditProvider auditProvider;
    private final UserService userService;
    private final UserQueryService userQueryService;

    private final NotifierService notifierService;

    @EventListener(RedisKeyExpiredEvent.class)
    public void onExpired(RedisKeyExpiredEvent<ForgotPassword> event) {
        if (!(event.getValue() instanceof ForgotPassword)) return;


    }

    public ForgotPassword generate(ForgotReqDTO.Send send, User user) {
        Duration expDuration = Duration.of(2, ChronoUnit.HOURS);

        Instant iss = Instant.now();
        Instant expired = iss.plus(expDuration);

        JwtResult ffs = JwtUtil.encode(Jwts.claims()
                .setId(UUID.randomUUID().toString())
                .setAudience("ffs")
                .setSubject(user.getId())
                .setIssuedAt(Date.from(iss))
                .setExpiration(Date.from(expired))
        );
        String otp = generateOtp();

        try {
            return repo.save(ForgotPassword.builder()
                    .uid(user.getId())
                    .token(ffs.getToken())
                    .otp(otp)
                    .ttl(expDuration.getSeconds())
                    .build());
        }
        finally {
            if (send == ForgotReqDTO.Send.TELEGRAM)
                sendViaTelegram(user.getTg().getId(), otp);
        }
    }

    @Transactional
    public ForgotPassword regenerate(String token) {
        Jws<Claims> jws = JwtUtil.decodeToken(token);
        Claims body = jws.getBody();

        if ("ffs".equals(body.getAudience())) {
            Duration expDuration = Duration.of(2, ChronoUnit.HOURS);
            ForgotPassword fp = repo
                    .findById(body.getSubject())
                    .orElseThrow(() -> new BadRequestException("invalid otp identity reset"));
            User user = userQueryService.findById(fp.getUid());

            repo.deleteById(fp.getUid());
            String otp = generateOtp();
            try {
                return repo.save(ForgotPassword.builder()
                        .uid(fp.getUid())
                        .otp(otp)
                        .token(token)
                        .ttl(expDuration.getSeconds())
                        .build());
            }
            finally {
                sendViaTelegram(user.getTg().getId(), otp);
            }
        }
        throw new BadRequestException("invalid otp identity reset");
    }

    public boolean validate(String token, String otp) {
        try {
            Jws<Claims> jws = JwtUtil.decodeToken(token);
            Claims body = jws.getBody();

            if ("ffs".equals(body.getAudience())) {
                ForgotPassword fp = repo
                        .findById(body.getSubject())
                        .orElseThrow(() -> new BadRequestException("invalid reset request token"));

                return fp.getOtp().equals(otp);
            }
            else throw new BadRequestException("invalid reset request token");
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Transactional
    public void reset(String userId, String newPassword) {
        User user = userQueryService.findById(userId);
        auditProvider.setName(user.getNik());
        userService.updatePassword(user, newPassword);

        repo.deleteById(userId);
        auditProvider.clear();
    }

    @Async
    public void sendViaTelegram(long telegramId, String otp) {
        notifierService.sendRaw(telegramId,
                "Kode OTP Lupa Password:",
                otp
        );
    }

    private String generateOtp() {
        Random random = new Random();
        int length = random.nextInt(7 - 6) + 6;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(10);
            sb.append(NUMBERS.charAt(index));
        }
        return sb.toString();
    }

}
