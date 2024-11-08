package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.request.CreateUserDTO;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.utils.AuthorityConstant;
import dev.scaraz.mars.common.utils.ConfigEntry;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.domain.order.Sto;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.IssueQueryService;
import dev.scaraz.mars.core.repository.db.credential.RoleRepo;
import dev.scaraz.mars.core.repository.db.order.IssueRepo;
import dev.scaraz.mars.core.repository.db.order.StoRepo;
import dev.scaraz.mars.core.service.credential.AccountService;
import dev.scaraz.mars.core.service.credential.RoleService;
import dev.scaraz.mars.core.service.order.IssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static dev.scaraz.mars.common.utils.AuthorityConstant.*;
import static dev.scaraz.mars.common.utils.ConfigConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class Initializer {

    private final Environment env;

    private final MarsProperties marsProperties;

    private final AccountService accountService;
    private final AccountQueryService accountQueryService;

    private final RoleRepo roleRepo;
    private final RoleService roleService;

    private final IssueRepo issueRepo;
    private final IssueService issueService;
    private final IssueQueryService issueQueryService;

    private final StoRepo stoRepo;

    private final ConfigService configService;


    public void checkWitel() {
        Witel witel = marsProperties.getWitel();
        if (witel == null)
            throw new IllegalStateException("Unknown Witel region, please set first from environtment 'MARS_WITEL'");
    }

    public void importConfig() {
        configService.bulkCreate(Tag.APPLICATION,
                new ConfigEntry<>(APP_ALLOW_AGENT_CREATE_TICKET_BOOL, false, "Agent diperbolehkan membuat tiket sendiri"),
                new ConfigEntry<>(APP_USER_REGISTRATION_APPROVAL_BOOL, true, "Registrasi user melalui bot telegram diperlukan persetujuan dari admin"),
                new ConfigEntry<>(APP_USER_REGISTRATION_APPROVAL_DRT, Duration.ofDays(1), "Lama waktu/Durasi registrasi disetujui"),
                new ConfigEntry<>(APP_ISSUE_GAUL_EXCLUDE_LIST, new ArrayList<String>(), "List Issue yang tidak dihitung sebagai Gangguan Ulang"),
                new ConfigEntry<>(APP_SOLUTION_REPORT_EXCLUDE_LIST, new ArrayList<String>(), "List Actual Solution yang dikeluarkan dari hitungan performance")
        );

        configService.bulkCreate(Tag.ACCOUNT,
                new ConfigEntry<>(ACC_EXPIRED_BOOL, true, "Akun disable secara otomatis"),
                new ConfigEntry<>(ACC_EXPIRED_DRT, Duration.ofDays(365), "Durasi akun sebelum terblokir"),
                new ConfigEntry<>(ACC_REGISTRATION_EMAILS_LIST, List.of(), "List email yang ditampilkan untuk keperluan persetujuan registrasi")
        );

        configService.bulkCreate(Tag.CREDENTIAL,
                new ConfigEntry<>(CRD_PASSWORD_ALGO_STR, "bcrypt"),
                new ConfigEntry<>(CRD_PASSWORD_HASH_ITERATION_INT, 24_200),
                new ConfigEntry<>(CRD_PASSWORD_SECRET_STR, () -> {
                    byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
                    byte[] bytes = new byte[16];

                    Random random = new Random();
                    random.nextBytes(bytes);

                    byte[] hexChars = new byte[bytes.length * 2];
                    for (int j = 0; j < bytes.length; j++) {
                        int v = bytes[j] & 0xFF;
                        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
                        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
                    }
                    return new String(hexChars, StandardCharsets.UTF_8);
                }),
                new ConfigEntry<>(CRD_PASSWORD_HISTORY_INT, 1,
                        "Menyimpan password sebelumnya sebanyak %n termasuk password sekarang, saat user melakukan pergantian password, " +
                                "sistem akan mengecek apakah password baru sama dengan password yang sebelumnya tersimpan"
                )
        );

        configService.bulkCreate(Tag.JWT,
                new ConfigEntry<>(JWT_TOKEN_EXPIRED_DRT, Duration.ofHours(2), "Durasi web token"),
                new ConfigEntry<>(JWT_TOKEN_REFRESH_EXPIRED_DRT, Duration.ofHours(12), "Durasi refresh web token")
        );

        configService.bulkCreate(Tag.TELEGRAM,
                new ConfigEntry<>(TG_CONFIRMATION_DRT,
                        Duration.ofMinutes(30),
                        "Lama waktu yang diperlukan untuk menunggu requestor menjawab konfirmasi sebelum tiket close"),
                new ConfigEntry<>(TG_PENDING_CONFIRMATION_DRT,
                        Duration.ofHours(1),
                        "Lama waktu menunggu untuk tiket dengan status PENDING"),
                new ConfigEntry<>(TG_START_CMD_ISSUE_COLUMN_INT,
                        3,
                        "Jumlah kolom perbaris pada command /start")
        );
    }

    @Transactional
    public void importRolesAndAdminAccount() {
        Role adminRole;
        if (roleRepo.existsByName(ADMIN_ROLE)) {
            adminRole = roleRepo.findByName(ADMIN_ROLE)
                    .orElseThrow();
        }
        else adminRole = roleService.create(ADMIN_ROLE);

        if (!roleRepo.existsByName(AGENT_ROLE))
            roleService.create(AGENT_ROLE);

        if (!roleRepo.existsByName(USER_ROLE))
            roleService.create(AuthorityConstant.USER_ROLE);

        if (!accountQueryService.existByNik("admin")) {
            log.debug("CREATE DEFAULT ADMIN USER");
            Account admin = accountService.create(CreateUserDTO.builder()
                    .name("Administrator")
                    .nik("admin")
                    .active(true)
                    .witel(marsProperties.getWitel())
                    .roles(List.of(adminRole.getId()))
                    .build());

            accountService.updatePassword(admin, "admin");
        }
    }

    @Transactional
    public void importIssue() {
        if (!env.acceptsProfiles(p -> p.test("dev"))) return;
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
    }

    public void importSto() {
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

//    private void createIssueInlineButton() {
//        log.info("(RE)CREATE ISSUE INLINE BUTTONS");
//
//        synchronized (ISSUES_BUTTON_LIST) {
//            MultiValueMap<Product, Issue> issuesMap = new LinkedMultiValueMap<>();
//            for (Issue issue : issueQueryService.findAllNotDeleted()) {
//                issuesMap.putIfAbsent(issue.getProduct(), new ArrayList<>());
//                issuesMap.get(issue.getProduct()).add(issue);
//            }
//
//            for (Product product : issuesMap.keySet()) {
//                List<InlineKeyboardButton> buttons = new ArrayList<>();
//                List<Issue> issues = Objects.requireNonNull(issuesMap.get(product));
//                for (Issue issue : issues) {
//                    String name = StringUtils.isNotBlank(issue.getAlias()) ?
//                            issue.getAlias() : issue.getName();
//
//                    buttons.add(InlineKeyboardButton.builder()
//                            .text(name)
//                            .callbackData(REPORT_ISSUE + issue.getId())
//                            .build());
//                }
//
//                ISSUES_BUTTON_LIST.put(product, buttons);
//            }
//        }
//    }

}
