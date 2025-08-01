package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.RoleReadDto;
import ru.otus.hw.dto.UserReadDto;
import ru.otus.hw.models.User;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserReadMapper {

    private final RoleReadMapper roleReadMapper;

    public UserReadDto map(User object) {
        Set<RoleReadDto> roles = object.getRoles().stream()
                .map(roleReadMapper::map)
                .collect(Collectors.toCollection(HashSet::new));
        return new UserReadDto(
                object.getId(),
                object.getUsername(),
                roles
        );
    }
}
