package com.jumkid.vehicle.config;

import com.jumkid.share.security.AccessScope;
import com.jumkid.share.service.InternalRestApiClient;
import com.jumkid.share.user.UserProfileManager;
import com.jumkid.vehicle.enums.VehicleField;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;

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

    @Bean
    public InternalRestApiClient internalRestApiClient(RestTemplate restTemplate,
                                                       UserProfileManager userProfileManager,
                                                       HttpServletRequest httpServletRequest) {
        return new InternalRestApiClient(restTemplate, userProfileManager, httpServletRequest);
    }

}
