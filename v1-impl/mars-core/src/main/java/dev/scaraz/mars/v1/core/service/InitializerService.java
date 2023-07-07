package dev.scaraz.mars.v1.core.service;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.request.CreateUserDTO;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.v1.core.domain.credential.Role;
import dev.scaraz.mars.v1.core.domain.credential.User;
import dev.scaraz.mars.v1.core.domain.event.RefreshIssueInlineButtons;
import dev.scaraz.mars.v1.core.domain.order.Issue;
import dev.scaraz.mars.v1.core.domain.order.Sto;
import dev.scaraz.mars.v1.core.query.IssueQueryService;
import dev.scaraz.mars.v1.core.query.UserQueryService;
import dev.scaraz.mars.v1.core.repository.credential.RoleRepo;
import dev.scaraz.mars.v1.core.repository.order.IssueRepo;
import dev.scaraz.mars.v1.core.repository.order.StoRepo;
import dev.scaraz.mars.v1.core.service.credential.RoleService;
import dev.scaraz.mars.v1.core.service.credential.UserService;
import dev.scaraz.mars.v1.core.service.order.IssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static dev.scaraz.mars.common.utils.AppConstants.Authority.*;
import static dev.scaraz.mars.common.utils.AppConstants.Telegram.ISSUES_BUTTON_LIST;
import static dev.scaraz.mars.common.utils.AppConstants.Telegram.REPORT_ISSUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitializerService {

    private final MarsProperties marsProperties;

    private final UserService userService;
    private final UserQueryService userQueryService;

    private final RoleRepo roleRepo;
    private final RoleService roleService;

    private final IssueRepo issueRepo;
    private final IssueService issueService;
    private final IssueQueryService issueQueryService;

    private final StoRepo stoRepo;

    private final AppConfigService appConfigService;


    public void checkWitel() {
        Witel witel = marsProperties.getWitel();
        if (witel == null)
            throw new IllegalStateException("Unknown Witel region, please set first from environtment 'MARS_WITEL'");
    }

    public void initAppConfigs() {
        appConfigService.getCloseConfirm_drt();
        appConfigService.getAllowLogin_bool();
        appConfigService.getRegistrationRequireApproval_bool();
        appConfigService.getSendRegistrationApproval_bool();
        appConfigService.getPostPending_drt();
        appConfigService.getApprovalDurationHour_drt();
        appConfigService.getApprovalAdminEmails_arr();
        appConfigService.getAllowAgentCreateTicket_bool();
        appConfigService.getTelegramStartIssueColumn_int();
    }

    @Transactional
    public void initRolesAndCreateAdmin() {
        Role adminRole;
        if (roleRepo.existsByName(ADMIN_ROLE)) {
            adminRole = roleRepo.findByName(ADMIN_ROLE)
                    .orElseThrow();
        }
        else adminRole = roleService.create(ADMIN_ROLE, 100);

        if (!roleRepo.existsByName(AGENT_ROLE))
            roleService.create(AGENT_ROLE, 2);

        if (!roleRepo.existsByName(USER_ROLE))
            roleService.create(AppConstants.Authority.USER_ROLE, 1);

        if (!userQueryService.existByNik(AppConstants.Authority.ADMIN_NIK)) {
            log.debug("CREATE DEFAULT ADMIN USER");
            User admin = userService.create(CreateUserDTO.builder()
                    .name("Administrator")
                    .phone("00000000000")
                    .nik(AppConstants.Authority.ADMIN_NIK)
                    .username("admin")
                    .active(true)
                    .witel(marsProperties.getWitel())
                    .roles(List.of(adminRole.getId()))
                    .build());

            userService.updatePassword(admin, "admin");
        }
    }

    @Async
    @Transactional
    public void initIssue() {
        Map<String, Product> names = Map.of("lambat", Product.INTERNET,
                "intermittent", Product.INTERNET,
                "tbb", Product.INTERNET,
                "blank", Product.IPTV,
                "login", Product.IPTV,
                "network", Product.IPTV,
                "matot", Product.VOICE,
                "bulk", Product.VOICE,
                "icog", Product.VOICE);

        for (String name : names.keySet()) {
            if (issueRepo.existsByNameAndProduct(name, names.get(name))) continue;
            issueService.create(name, names.get(name), null);
        }

        createIssueInlineButton();
    }

    @Async
    public void initSto() {
        try (InputStream is = getClass().getResourceAsStream("/list-sto.csv")) {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            String[] lines = content.split("\n");

            for (String line : lines) {
                if (StringUtils.isBlank(line)) continue;
                String[] splited = line.split(";");

                String witel = splited[0],
                        datel = splited[1],
                        alias = splited[2],
                        name = splited[3];

                Witel w;
                try {
                    w = Witel.valueOf(witel.toUpperCase());
                }
                catch (IllegalArgumentException ex) {
                    continue;
                }

                if (stoRepo.existsByWitelAndAlias(w, alias)) continue;
                stoRepo.save(Sto.builder()
                        .witel(w)
                        .datel(datel)
                        .alias(alias)
                        .name(name)
                        .build());
            }
        }
        catch (Exception e) {
        }
    }

    private void createIssueInlineButton() {
        log.info("(RE)CREATE ISSUE INLINE BUTTONS");

        synchronized (ISSUES_BUTTON_LIST) {
            MultiValueMap<Product, Issue> issuesMap = new LinkedMultiValueMap<>();
            for (Issue issue : issueQueryService.findAll()) {
                issuesMap.putIfAbsent(issue.getProduct(), new ArrayList<>());
                issuesMap.get(issue.getProduct()).add(issue);
            }

            for (Product product : issuesMap.keySet()) {

                List<InlineKeyboardButton> buttons = new ArrayList<>();
                List<Issue> issues = Objects.requireNonNull(issuesMap.get(product));
                for (Issue issue : issues) {
                    String name = Objects.requireNonNullElse(
                            issue.getAlias(),
                            issue.getName()
                    );
                    buttons.add(InlineKeyboardButton.builder()
                            .text(name)
                            .callbackData(REPORT_ISSUE + issue.getId())
                            .build());
                }

                ISSUES_BUTTON_LIST.put(product, buttons);
            }
        }
    }

    @Async
    @EventListener(classes = RefreshIssueInlineButtons.class)
    public void onResetInlineButton() {
        createIssueInlineButton();
    }

}
