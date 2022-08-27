package com.epam.esm.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;

import static com.epam.esm.security.KeycloakUtil.APP_USER_ID_CLAIM_NAME;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String SIGNUP_ENDPOINT = "/api/users/signup";
    private static final String LOGIN_ENDPOINT = "/api/users/login";
    private static final String MAIN_ENTITY_ENDPOINT = "/api/certificates/**";

    private AuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement()
                    .sessionCreationPolicy(STATELESS)
                    .and()
                .authorizeRequests()
                    .antMatchers(SIGNUP_ENDPOINT, LOGIN_ENDPOINT).permitAll()
                    .antMatchers(HttpMethod.GET, MAIN_ENTITY_ENDPOINT).permitAll()
                    .anyRequest().authenticated()
                    .and()
                .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .and()
                .oauth2ResourceServer(
                        configurer -> configurer.jwt(
                                jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        ApplicationJwtGrantedAuthoritiesConverter authoritiesConverter = new ApplicationJwtGrantedAuthoritiesConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        jwtAuthenticationConverter.setPrincipalClaimName(APP_USER_ID_CLAIM_NAME);
        return jwtAuthenticationConverter;
    }
}
