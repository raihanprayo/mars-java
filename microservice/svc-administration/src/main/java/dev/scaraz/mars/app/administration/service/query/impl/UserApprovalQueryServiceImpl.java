package dev.scaraz.mars.app.administration.service.query.impl;

import dev.scaraz.mars.app.administration.domain.db.UserApproval;
import dev.scaraz.mars.app.administration.repository.db.UserApprovalRepo;
import dev.scaraz.mars.app.administration.service.query.UserApprovalQueryService;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class UserApprovalQueryServiceImpl implements UserApprovalQueryService {

    private final UserApprovalRepo repo;

    @Override
    public List<UserApproval> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<UserApproval> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public UserApproval findByIdOrNo(String idOrNo) {
        return repo.findByIdOrNo(idOrNo, idOrNo)
                .orElseThrow(() -> NotFoundException.args("data registrasi tidak ditemukan"));
    }

}
