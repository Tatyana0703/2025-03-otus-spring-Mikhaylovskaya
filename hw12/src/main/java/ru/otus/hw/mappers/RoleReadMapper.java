package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.RoleReadDto;
import ru.otus.hw.models.Role;

@Component
public class RoleReadMapper {

    public RoleReadDto map(Role object) {
        return new RoleReadDto(
                object.getId(),
                object.getName()
        );
    }
}
