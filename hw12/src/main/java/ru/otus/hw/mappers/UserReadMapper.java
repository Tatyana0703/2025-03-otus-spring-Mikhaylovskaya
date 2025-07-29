package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.RoleReadDto;
import ru.otus.hw.dto.UserReadDto;
import ru.otus.hw.models.User;

@Component
@RequiredArgsConstructor
public class UserReadMapper {

    private final RoleReadMapper roleReadMapper;

    public UserReadDto map(User object) {
        RoleReadDto role = roleReadMapper.map(object.getRole());
        return new UserReadDto(
                object.getId(),
                object.getUsername(),
                role
        );
    }
}
