package dev.scaraz.mars.common.tools.converter;

import dev.scaraz.mars.common.tools.enums.*;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

public interface EnumsConverter {

    class StringToProduct implements Converter<String, Product> {
        @Override
        public Product convert(String source) {
            if (StringUtils.isBlank(source)) return null;
            return EnumUtils.getEnum(Product.class, source.toUpperCase());
        }
    }

    class StringToAgStatus implements Converter<String, AgStatus> {
        @Override
        public AgStatus convert(String source) {
            if (StringUtils.isBlank(source)) return null;
            return EnumUtils.getEnum(AgStatus.class, source.toUpperCase());
        }
    }

    class StringToTcSource implements Converter<String, TcSource> {
        @Override
        public TcSource convert(String source) {
            if (StringUtils.isBlank(source)) return null;
            return EnumUtils.getEnum(TcSource.class, source.toUpperCase());
        }
    }

    class StringToTcStatus implements Converter<String, TcStatus> {
        @Override
        public TcStatus convert(String source) {
            if (StringUtils.isBlank(source)) return null;
            return EnumUtils.getEnum(TcStatus.class, source.toUpperCase());
        }
    }

    class StringToWitel implements Converter<String, Witel> {
        @Override
        public Witel convert(String source) {
            if (StringUtils.isBlank(source)) return null;
            return EnumUtils.getEnum(Witel.class, source.toUpperCase());
        }
    }
}
