package dev.scaraz.mars.admin.config;

import dev.scaraz.mars.admin.config.properties.KeycloakServerProperties;
import org.keycloak.Config;
import org.keycloak.exportimport.ExportImportManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakTransactionManager;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.resources.KeycloakApplication;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicReference;

public class KeycloakEmbeddedApplication extends KeycloakApplication {
    public static final AtomicReference<ApplicationContext> context = new AtomicReference<>();

    private final KeycloakServerProperties serverProperties;

    public KeycloakEmbeddedApplication() {
        super();
        ApplicationContext ctx = context.get();
        serverProperties = ctx.getBean(KeycloakServerProperties.class);
    }

    @Override
    protected void loadConfig() {
        Config.init(new Config.SystemPropertiesConfigProvider());
    }

    @Override
    protected ExportImportManager bootstrap() {
        ExportImportManager bootstrap = super.bootstrap();
        applicationBootstrap();
        return bootstrap;
    }

    private void applicationBootstrap() {
        KeycloakSession session = getSessionFactory().create();
        ApplianceBootstrap ab = new ApplianceBootstrap(session);

        KeycloakTransactionManager tm = session.getTransactionManager();
        tm.begin();
        ab.createMasterRealm();
        ab.createMasterRealmUser(serverProperties.getAdmin().getUsername(), serverProperties.getAdmin().getPassword());
        tm.commit();

        session.close();
    }

}
