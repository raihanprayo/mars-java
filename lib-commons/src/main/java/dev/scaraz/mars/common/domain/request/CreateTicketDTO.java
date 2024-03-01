package dev.scaraz.mars.common.domain.request;

import dev.scaraz.mars.common.domain.general.TicketBotForm;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketDTO {
    private String incident;
    private String issue;
    private String service;
    private Product product;
    private TcSource source;

    private Witel witel;
    private String sto;

    public CreateTicketDTO(TicketBotForm form) {
        this.incident = form.getIncident();
        this.issue = form.getIssue();
        this.service = form.getService();
        this.product = form.getProduct();
        this.source = form.getSource();
        this.witel = form.getWitel();
        this.sto = form.getSto();
    }
}
