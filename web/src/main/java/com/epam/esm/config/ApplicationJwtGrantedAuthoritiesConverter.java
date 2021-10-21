package com.epam.esm.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collection;

public class ApplicationJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final String SCOPE_CLAIM = "scope";
    private static final String WHITESPACE = " ";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        return Arrays.stream(jwt.getClaim(SCOPE_CLAIM)
                        .toString()
                        .split(WHITESPACE))
                .map(authority -> (GrantedAuthority) new SimpleGrantedAuthority(authority))
                .toList();
    }
}
