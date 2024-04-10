package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.common.config.DataSourceAuditor;
import dev.scaraz.mars.common.domain.request.ForgotReqDTO;
import dev.scaraz.mars.common.domain.response.JwtResult;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.core.config.datasource.AuditProvider;
import dev.scaraz.mars.core.domain.cache.ForgotPassword;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.repository.cache.ForgotPasswordRepo;
import dev.scaraz.mars.core.service.NotifierService;
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
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private static final String NUMBERS = "0123456789";

    private final ForgotPasswordRepo repo;

    private final AuditProvider auditProvider;
    private final AccountService accountService;
    private final AccountQueryService accountQueryService;

    private final NotifierService notifierService;

    @EventListener(RedisKeyExpiredEvent.class)
    public void onExpired(RedisKeyExpiredEvent<ForgotPassword> event) {
        if (!(event.getValue() instanceof ForgotPassword)) return;


    }

    public ForgotPassword generate(ForgotReqDTO.Send send, Account account) {
        Optional<ForgotPassword> fpOpt = repo.findById(account.getId());
        if (fpOpt.isEmpty()) {
            Duration expDuration = Duration.of(10, ChronoUnit.MINUTES);

            Instant iss = Instant.now();
            Instant expired = iss.plus(expDuration);

            JwtResult ffs = JwtUtil.encode(Jwts.claims()
                    .id(UUID.randomUUID().toString())
                    .subject(account.getId())
                    .issuedAt(Date.from(iss))
                    .expiration(Date.from(expired))
                    .audience()
                    .add("ffs")
                    .and()
                    .build()
            );
            String otp = generateOtp();

            try {
                return repo.save(ForgotPassword.builder()
                        .uid(account.getId())
                        .token(ffs.getToken())
                        .otp(otp)
                        .expiredAt(ffs.getExpiredAt().toEpochMilli())
                        .ttl(expDuration.getSeconds())
                        .build());
            }
            finally {
                if (send == ForgotReqDTO.Send.TELEGRAM)
                    sendViaTelegram(account.getTg().getId(), otp);
            }
        }
        else {
            return fpOpt
                    .orElseThrow(() -> NotFoundException.entity(ForgotPassword.class, "id", account.getId()));
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
            Account account = accountQueryService.findById(fp.getUid());

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
                sendViaTelegram(account.getTg().getId(), otp);
            }
        }
        throw new BadRequestException("invalid otp identity reset");
    }

    public boolean validate(String token, String otp) {
        try {
            Jws<Claims> jws = JwtUtil.decodeToken(token);
            Claims body = jws.getPayload();

            if (body.getAudience().contains("ffs")) {
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
        Account account = accountQueryService.findById(userId);
        DataSourceAuditor.setUsername(account.getUsername());
        accountService.updatePassword(account, newPassword);

        repo.deleteById(userId);
        DataSourceAuditor.clear();
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
