package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.credential.Group;
import dev.scaraz.mars.core.query.GroupQueryService;
import dev.scaraz.mars.core.repository.credential.GroupRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
public class GroupQueryServiceImpl extends QueryBuilder implements GroupQueryService {
    private final GroupRepo repo;

}
