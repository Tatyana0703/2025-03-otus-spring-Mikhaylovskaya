package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.UserCreateEditDto;
import ru.otus.hw.security.SecurityUserDetails;
import ru.otus.hw.dto.UserReadDto;
import ru.otus.hw.mappers.UserCreateEditMapper;
import ru.otus.hw.mappers.UserReadMapper;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.UserRepository;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    private final UserReadMapper userReadMapper;

    private final UserCreateEditMapper userCreateEditMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new SecurityUserDetails(
                        user.getUsername(),
                        user.getPassword(),
                        user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_%s".formatted(role.getName())))
                                .collect(Collectors.toCollection(ArrayList::new))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user: %s".formatted(username)));
    }

    @Override
    @Transactional
    public UserReadDto create(UserCreateEditDto userDto) {
        User user = userCreateEditMapper.map(userDto);
        userRepository.save(user);
        return userReadMapper.map(user);
    }
}
