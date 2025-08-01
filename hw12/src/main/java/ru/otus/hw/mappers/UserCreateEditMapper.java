package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.UserCreateEditDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Role;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.RoleRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserCreateEditMapper {

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

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
        userDto.getRoleIds().forEach(roleId -> {
            Role role = roleRepository.findById(roleId).orElseThrow(() ->
                    new NotFoundException("Role with id %d not found".formatted(roleId)));
            user.addRole(role);
        });
        user.setUsername(userDto.getUsername());
        String password = Optional.of(userDto.getRawPassword()).map(passwordEncoder::encode)
                .orElseThrow(() -> new NotFoundException("User password not found for username %s"
                        .formatted(userDto.getUsername())));
        user.setPassword(password);
    }
}
