package dev.scaraz.mars.common.domain.general;

import dev.scaraz.mars.common.tools.annotation.FormDescriptor;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TicketBotForm {

    @FormDescriptor
    private Witel witel;

    @FormDescriptor
    private String sto;

    @FormDescriptor(
            alias = {"no incident", "incidentno", "Tiket NOSSA"})
    private String incident;

    @FormDescriptor(
            required = true,
            alias = {"jenis gangguan", "problemtype", "problem"})
    private String issue;
    private Long issueId;

    @FormDescriptor(
            required = true,
            alias = {"no service", "serviceno"})
    private String service;

    @FormDescriptor(
            required = true,
            alias = {"jenis layanan", "producttype"})
    private Product product;

    @FormDescriptor(
            multiline = true,
            alias = {"description", "deskripsi"})
    private String note;

    private TcSource source;

    private long senderId;
    private String senderName;

    private static final Map<String, FormDescriptor> descriptors;
    public static Map<String, FormDescriptor> getDescriptors() {
        if (descriptors != null) return descriptors;

        Map<String, FormDescriptor> map = new TreeMap<>();
        for (Field field : TicketBotForm.class.getDeclaredFields()) {
            FormDescriptor desc = field.getAnnotation(FormDescriptor.class);
            if (desc != null) map.put(field.getName(), desc);
        }
        return map;
    }

    static {
        descriptors = Map.copyOf(getDescriptors());
    }
}
