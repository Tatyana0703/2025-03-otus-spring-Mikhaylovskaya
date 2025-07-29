package ru.otus.hw.dto;

import lombok.Value;
import org.springframework.security.core.GrantedAuthority;

@Value
public class Authority implements GrantedAuthority {

    private String role;

    @Override
    public String getAuthority() {
        return "ROLE_%s".formatted(role);
    }
}
