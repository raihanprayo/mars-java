package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.request.CreateUserDTO;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.Issue;
import dev.scaraz.mars.core.query.IssueQueryService;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.repository.credential.GroupRepo;
import dev.scaraz.mars.core.repository.credential.RoleRepo;
import dev.scaraz.mars.core.repository.order.IssueRepo;
import dev.scaraz.mars.core.service.credential.GroupService;
import dev.scaraz.mars.core.service.credential.RoleService;
import dev.scaraz.mars.core.service.credential.UserService;
import dev.scaraz.mars.core.service.order.IssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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

    private final GroupRepo groupRepo;
    private final GroupService groupService;

    private final RoleRepo roleRepo;
    private final RoleService roleService;

    private final IssueRepo issueRepo;
    private final IssueService issueService;
    private final IssueQueryService issueQueryService;

    private final AppConfigService appConfigService;


    public void checkWitel() {
        Witel witel = marsProperties.getWitel();
        if (witel == null)
            throw new IllegalStateException("Unknown Witel region, please set first from environtment 'MARS_WITEL'");
    }

    @Async
    @Transactional
    public void preInitRolesAndCreateAdmin() {
        Role adminRole;
        if (roleRepo.existsByName(ADMIN_ROLE)) {
            adminRole = roleRepo.findByName(ADMIN_ROLE)
                    .orElseThrow();
        }
        else adminRole = roleService.create(ADMIN_ROLE, 100);

        if (!roleRepo.existsByName(USER_ROLE))
            roleService.create(AppConstants.Authority.USER_ROLE, 1);

        if (!roleRepo.existsByName(USER_DASHBOARD_ROLE))
            roleService.create(USER_DASHBOARD_ROLE, 2);

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
    public void preInitGroups() {
        Map<String, Boolean> names = Map.of(
                "ROC Assurance", true,
                "ROC TIAL", true,
                "Banten", false,
                "Bekasi", false,
                "Bogor", false,
                "Jakbar", false,
                "Jakpus", false,
                "Jaktim", false,
                "Jakut", false,
                "Tangerang", false);

        for (String name : names.keySet()) {
            boolean groupExist = groupRepo.findByName(name).isPresent();
            if (!groupExist) groupService.create(name, names.get(name));
        }
    }

    @Async
    @Transactional
    public void preInitIssue() {
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

    public void createIssueInlineButton() {
        log.info("(RE)CREATE ISSUE INLINE BUTTONS");

        MultiValueMap<Product, Issue> issues = new LinkedMultiValueMap<>();
        for (Issue issue : issueQueryService.findAll()) {
            issues.putIfAbsent(issue.getProduct(), new ArrayList<>());
            issues.get(issue.getProduct()).add(issue);
        }

        log.info("Iterate Product");
        for (Product product : issues.keySet()) {

            List<InlineKeyboardButton> buttons = new ArrayList<>();
            for (Issue issue : Objects.requireNonNull(issues.get(product))) {

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

        log.debug("ISSUE BUTTONS CACHE {}", ISSUES_BUTTON_LIST);
    }

    @Async
    public void preInitAppConfigs() {
        appConfigService.getCloseConfirm_int();
        appConfigService.getAllowLogin_bool();
        appConfigService.getRegistrationRequireApproval_bool();
        appConfigService.getSendRegistrationApproval_bool();
        appConfigService.getPostPending_int();
        appConfigService.getApprovalDurationHour_int();
        appConfigService.getApprovalAdminEmails_arr();
    }

    @Async
    @EventListener(value = String.class, condition = "event.equals('recreate-inline-btn')")
    public void onResetInlineButton(String event) {
        createIssueInlineButton();
    }

}
