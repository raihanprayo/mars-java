package dev.scaraz.mars.common.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormDescriptor {
    boolean required() default false;

    boolean multiline() default false;

    String[] alias() default {};


    class Util {
        public static Map<String, FormDescriptor> collectFieldDescriptors(Class<?> type) {
            Map<String, FormDescriptor> map = new TreeMap<>();
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;

                FormDescriptor desc = field.getAnnotation(FormDescriptor.class);
                if (desc != null) map.put(field.getName(), desc);
            }
            return map;
        }
    }

}
