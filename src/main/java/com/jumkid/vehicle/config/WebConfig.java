package com.jumkid.vehicle.config;

import com.jumkid.share.security.AccessScope;
import com.jumkid.vehicle.enums.VehicleField;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToVehicleFieldConverter());
        registry.addConverter(new StringToAccessScopeConverter());
    }

    private static class StringToAccessScopeConverter implements Converter<String, AccessScope> {
        @Override
        public AccessScope convert(String source) {
            return AccessScope.valueOf(source.toUpperCase());
        }
    }

    private static class StringToVehicleFieldConverter implements Converter<String, VehicleField> {
        @Override
        public VehicleField convert(String source) {
            return VehicleField.valueOf(source.toUpperCase());
        }
    }

}
