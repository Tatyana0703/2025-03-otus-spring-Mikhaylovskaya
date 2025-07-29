package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.RoleReadDto;
import ru.otus.hw.mappers.RoleReadMapper;
import ru.otus.hw.repositories.RoleRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final RoleReadMapper roleReadMapper;

    @Override
    public List<RoleReadDto> findAll() {
        return roleRepository.findAll().stream()
                .map(roleReadMapper::map)
                .toList();
    }
}