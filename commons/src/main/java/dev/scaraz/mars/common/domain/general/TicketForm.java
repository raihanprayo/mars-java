package dev.scaraz.mars.common.domain.general;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TicketForm {

    @Descriptor(required = true)
    private Witel witel;

    @Descriptor(required = true)
    private String sto;

    @Descriptor(
            required = true,
            alias = {"no tiket", "incidentno"})
    private String incident;

    @Descriptor(
            required = true,
            alias = {"jenis gangguan", "problemtype", "problem"})
    private String issue;

    @Descriptor(
            required = true,
            alias = {"no service", "serviceno"})
    private String service;

    @Descriptor(
            required = true,
            alias = {"jenis layanan", "producttype"})
    private Product product;

    @Descriptor(
            multiline = true,
            alias = {"note"})
    private String description;

    private TcSource source;

    private long senderId;
    private String senderName;

    private static final Map<String, Descriptor> descriptors;
    public static Map<String, Descriptor> getDescriptors() {
        if (descriptors != null) return descriptors;

        Map<String, Descriptor> map = new TreeMap<>();
        for (Field field : TicketForm.class.getDeclaredFields()) {
            Descriptor desc = field.getAnnotation(Descriptor.class);
            if (desc != null) map.put(field.getName(), desc);
        }
        return map;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Descriptor {
        boolean required() default false;

        boolean multiline() default false;

        String[] alias() default {};
    }

    static {
        descriptors = Map.copyOf(getDescriptors());
    }
}
