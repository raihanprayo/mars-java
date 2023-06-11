package dev.scaraz.mars.user.initializer;

import dev.scaraz.mars.security.MarsSecurityProperties;
import dev.scaraz.mars.user.domain.db.AppConfig;
import dev.scaraz.mars.user.domain.db.AppConfigCategory;
import dev.scaraz.mars.user.repository.db.AppConfigRepo;
import dev.scaraz.mars.user.service.AppConfigService;
import dev.scaraz.mars.user.service.ScriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtConfigScript {

    private static final String SCRIPT = "jwt-config";

    private final AppConfigRepo repo;
    private final AppConfigService configService;
    private final MarsSecurityProperties securityProperties;
    private final ScriptService scriptService;

    @Autowired
    @Transactional
    public void exec() {
        if (scriptService.isExecuted(SCRIPT)) return;

        AppConfigCategory category = configService.getCategory(AppConfigService.JWT.CATEGORY);

        List<AppConfig> configs = List.of(
                configService.create(
                        AppConfigService.JWT.PREFIX,
                        securityProperties.getJwt().getTokenPrefix(),
                        null),
                configService.create(
                        AppConfigService.JWT.EXPIRED_DURATION,
                        securityProperties.getJwt().getTokenDuration(),
                        null),
                configService.create(
                        AppConfigService.JWT.REFRESH_EXPIRED_DURATION,
                        securityProperties.getJwt().getRefreshTokenDuration(),
                        null)
        );

        for (AppConfig config : configs) {
            if (repo.existsByNameAndCategoryId(config.getName(), category.getId()))
                continue;

            config.setCategory(category);
            configService.save(config);
        }
        scriptService.updateAsExecuted(SCRIPT);
    }

}
