package dev.scaraz.mars.telegram.model;

import java.lang.annotation.Annotation;
import java.util.List;

public interface TelegramAnnotationArgResolver extends TelegramArgResolver {
    List<Class<? extends Annotation>> DEFAULT_SUPP_ANNOTATIONS = List.of();

    default List<Class<? extends Annotation>> supportedAnnotations() {
        return DEFAULT_SUPP_ANNOTATIONS;
    }
}
