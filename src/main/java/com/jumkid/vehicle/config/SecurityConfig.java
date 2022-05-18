package com.jumkid.vehicle.config;

import com.jumkid.share.security.BearerTokenRequestFilter;
import com.jumkid.share.user.UserProfileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${jwt.token.enable}")
    private boolean enableTokenCheck;

    @Value("${jwt.token.introspect.url}")
    private String tokenIntrospectUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public SecurityConfig(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**").permitAll() //whitelist
                .antMatchers(enableTokenCheck? "/**" : "/admin-console").authenticated() //any other request
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable();  // enable this if the authorization service exposure to public

        http.addFilterBefore(new BearerTokenRequestFilter(enableTokenCheck, tokenIntrospectUrl, restTemplate),
                UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public UserProfileManager userProfileManager(RestTemplate restTemplate) {
        return new UserProfileManager(restTemplate);
    }

}
