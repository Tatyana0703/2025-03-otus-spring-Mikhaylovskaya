package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.otus.hw.dto.UserCreateEditDto;
import ru.otus.hw.models.User;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserCreateEditMapper {

    private final PasswordEncoder passwordEncoder;

    public User map(UserCreateEditDto userDto, User user) {
        copy(userDto, user);
        return user;
    }

    public User map(UserCreateEditDto userDto) {
        User user = new User();
        copy(userDto, user);
        return user;
    }

    private void copy(UserCreateEditDto userDto, User user) {
        user.setUsername(userDto.getUsername());
        Optional.ofNullable(userDto.getRawPassword())
                .filter(StringUtils::hasText)
                .map(passwordEncoder::encode)
                .ifPresent(user::setPassword);
    }
}
