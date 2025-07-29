package ru.otus.hw.services;

import ru.otus.hw.dto.RoleReadDto;
import java.util.List;

public interface RoleService {

    List<RoleReadDto> findAll();
}