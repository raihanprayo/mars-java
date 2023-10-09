package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.general.TicketBotForm;
import dev.scaraz.mars.common.exception.telegram.TgInvalidFormError;
import dev.scaraz.mars.common.tools.annotation.FormDescriptor;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.tools.filter.type.ProductFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.core.domain.order.Issue;
import dev.scaraz.mars.core.domain.order.Sto;
import dev.scaraz.mars.core.query.IssueQueryService;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import dev.scaraz.mars.core.repository.db.order.StoRepo;
import dev.scaraz.mars.common.utils.Util;
import dev.scaraz.mars.security.MarsUserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor

@Component
public class TicketFormService {

    private final IssueQueryService issueQueryService;
    private final StoRepo stoRepo;

    public TicketBotForm parseTicketRegistration(String text) {
        TicketBotForm form = new TicketBotForm();
        String[] lines = text.split("\n");

        // parsing required field
        for (String line : lines) {
            line = line.trim();
            int colonIndex = line.indexOf(":");

            if (colonIndex == -1) continue;
            String fieldValue = line.substring(colonIndex + 1).trim();
            String fieldName = fieldNameMatcher(line.substring(0, colonIndex));

            if (fieldName == null || fieldName.equals("note")) continue;
            applyForm(form, fieldValue, fieldName);
        }

        return form;
    }

    public void parseTicketNote(TicketBotForm form, String text, List<String> addtFieldAliases) {
        String[] lines = text.split("\n");

        // parsing note/description field
        int noteFieldIndex = noteLineMatcher(lines, addtFieldAliases);
        if (noteFieldIndex != -1) {
            String noteStart = lines[noteFieldIndex];

            List<String> noteValue = Stream.concat(
                    Stream.of(noteStart.substring(noteStart.indexOf(":") + 1)),
                    List.of(lines).subList(noteFieldIndex + 1, lines.length).stream()
            ).collect(Collectors.toList());

            form.setNote(String.join(" ", noteValue));
        }
    }

    public void applyForm(TicketBotForm form, String fieldValue, String fieldName) {
        try {
            switch (fieldName) {
                case "witel":
                    form.setWitel(Witel.valueOf(fieldValue.toUpperCase()));
                    break;
                case "sto":
                    form.setSto(fieldValue.toUpperCase());
                    break;
                case "incident":
                    form.setIncident(fieldValue);
                    break;
                case "issue":
                    form.setIssue(fieldValue);
                    break;
                case "service":
                    form.setService(fieldValue);
                    break;
                case "product":
                    form.setProduct(Product.valueOf(fieldValue.toUpperCase()));
                    break;
            }
        }
        catch (IllegalArgumentException ex) {
            Class<? extends Enum<?>> enumType = fieldName.equals("witel") ?
                    Witel.class : Product.class;

            throw new TgInvalidFormError(
                    fieldName,
                    "error.ticket.form.enum",
                    Util.enumToString("/", enumType)
            );
        }
    }


    public String fieldNameMatcher(String fieldName) {
        Map<String, FormDescriptor> descriptors = TicketBotForm.getDescriptors();
        Set<String> formKeys = descriptors.keySet();
        for (String formKey : formKeys) {
            FormDescriptor formDescriptor = descriptors.get(formKey);
            if (formDescriptor.multiline()) continue;

            Stream<String> aliases = Stream.of(formDescriptor.alias());
            if (aliases.anyMatch(alias -> alias.equalsIgnoreCase(fieldName))) {
                return formKey;
            }

            if (formKey.equalsIgnoreCase(fieldName))
                return formKey;
        }

        return null;
    }


    // Privates
    public void checkFieldIncidentNo(TicketBotForm form) {
        final String FIELD_NAME = "incident";
        FormDescriptor formDescriptor = TicketBotForm.getDescriptors().get(FIELD_NAME);
        String incident = form.getIncident();
        if (!incident.toUpperCase().startsWith("IN")) {
            throw new TgInvalidFormError(
                    FIELD_NAME,
                    "error.ticket.form.incident",
                    List.of(formDescriptor.alias())
            );
        }

        String substring = incident.substring(2);
        if (!Util.isStringNumber(substring)) {
            throw new TgInvalidFormError(
                    FIELD_NAME,
                    "error.ticket.form.incident",
                    List.of(formDescriptor.alias())
            );
        }
    }

    public void checkFieldServiceNo(TicketBotForm form) {
        final String FIELD_NAME = "service";
        FormDescriptor formDescriptor = TicketBotForm.getDescriptors().get(FIELD_NAME);
        String serviceNo = form.getService();

        boolean isIptvInternet = List.of(Product.IPTV, Product.INTERNET).contains(form.getProduct());
        if (isIptvInternet) {
            boolean strNumbrAndPrefix1 = Util.isStringNumber(serviceNo) &&
                    serviceNo.startsWith("1");

            if (!strNumbrAndPrefix1) {
                throw new TgInvalidFormError(
                        FIELD_NAME,
                        "error.ticket.form.service",
                        List.of(formDescriptor.alias())
                );
            }
        }
        else {
            boolean prefixPlus = serviceNo.startsWith("+");
            boolean strNumbr = Util.isStringNumber(prefixPlus ?
                    serviceNo.substring(1) :
                    serviceNo
            );

            if (!strNumbr) {
                throw new TgInvalidFormError(
                        FIELD_NAME,
                        "error.ticket.form.service",
                        List.of(formDescriptor.alias())
                );
            }
        }
    }

    public void checkFieldIssue(TicketBotForm form) {
        final String FIELD_NAME = "issue";
        FormDescriptor formDescriptor = TicketBotForm.getDescriptors().get(FIELD_NAME);

        String problem = form.getIssue();
        IssueCriteria criteria = IssueCriteria.builder()
                .name(new StringFilter().setLike(problem))
                .product(new ProductFilter().setEq(form.getProduct()))
                .build();

        Optional<Issue> issueOpt = issueQueryService.findOne(criteria);

        if (issueOpt.isPresent()) {
            form.setIssueId(issueOpt.get().getId());
        }
        else {
            throw new TgInvalidFormError(
                    FIELD_NAME,
                    "error.ticket.form.problem",
                    List.of(formDescriptor.alias())
            );
        }
    }

    public void checkFieldSto(TicketBotForm form) {
        final String FIELD_NAME = "sto";
        FormDescriptor formDescriptor = TicketBotForm.getDescriptors().get(FIELD_NAME);

        Witel witel = Objects.requireNonNullElseGet(form.getWitel(), () -> MarsUserContext.getAccessToken().getWitel());
        String stoAlias = Objects.requireNonNullElseGet(form.getSto(), () -> MarsUserContext.getAccessToken().getSto());

        Optional<Sto> stoOpt = stoRepo.findByWitelAndAliasIgnoreCase(witel, stoAlias);
        if (stoOpt.isEmpty()) {
            List<Sto> stos = stoRepo.findAllByWitel(witel);
            String rundownList;

            if (stos.isEmpty()) {
                rundownList = "*_Empty list_*";
            }
            else {
                rundownList = stos.stream()
                        .map(Sto::getAlias)
                        .map(alias -> "- *" + alias + "*")
                        .collect(Collectors.joining("\n"));
            }

            throw new TgInvalidFormError(
                    FIELD_NAME,
                    "error.ticket.form.sto",
                    witel,
                    rundownList);
        }
    }


    public TicketBotForm checkInstantForm(TicketBotForm form) {
        if (form.getService() != null)
            checkFieldServiceNo(form);
        else
            throw new TgInvalidFormError("No Service", "Nilai kosong");

        if (form.getIncident() != null)
            checkFieldIncidentNo(form);

        return form;
    }

    private int noteLineMatcher(String[] lines, @Nullable List<String> addtFieldAliases) {
        String FIELD_NAME = "note";
        FormDescriptor formDescriptor = TicketBotForm.getDescriptors().get(FIELD_NAME);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            int colonIndex = line.indexOf(":");

            if (colonIndex == -1) continue;
            String fieldName = line.substring(0, colonIndex);

            List<String> aliases = new ArrayList<>(List.of(formDescriptor.alias()));
            if (addtFieldAliases != null && !addtFieldAliases.isEmpty())
                aliases.addAll(addtFieldAliases);

            Stream<String> aliasesStream = aliases.stream();
            if (aliasesStream.anyMatch(alias -> alias.equalsIgnoreCase(fieldName))) {
                return i;
            }
            else if (fieldName.equalsIgnoreCase(FIELD_NAME)) {
                return i;
            }
        }
        return -1;
    }

}
