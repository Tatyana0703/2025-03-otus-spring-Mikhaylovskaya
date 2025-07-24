package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.UserReadDto;
import ru.otus.hw.models.User;

@Component
@RequiredArgsConstructor
public class UserReadMapper {

    public UserReadDto map(User object) {
        return new UserReadDto(
                object.getId(),
                object.getUsername(),
                object.getRole()
        );
    }
}
