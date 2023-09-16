package dev.scaraz.mars.app.witel.service.app.impl;

import dev.scaraz.mars.app.witel.domain.Issue;
import dev.scaraz.mars.app.witel.domain.IssueParam;
import dev.scaraz.mars.app.witel.repository.IssueParamRepo;
import dev.scaraz.mars.app.witel.repository.IssueRepo;
import dev.scaraz.mars.app.witel.service.app.IssueService;
import dev.scaraz.mars.app.witel.service.query.IssueQueryService;
import dev.scaraz.mars.common.domain.request.CreateIssueDTO;
import dev.scaraz.mars.common.domain.request.UpdateIssueDTO;
import dev.scaraz.mars.common.domain.response.IssueParamDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor

@Service
public class IssueServiceImpl implements IssueService {

    private final IssueRepo repo;
    private final IssueParamRepo paramRepo;

    private final IssueQueryService queryService;

    @Override
    public Issue save(Issue issue) {
        return repo.save(issue);
    }

    @Override
    @Transactional
    public Issue create(CreateIssueDTO dto) {
        log.info("CREATE NEW ISSUE {}/{}", dto.getProduct(), dto.getName());

        if (repo.existsByWitelAndCodeAndProduct(dto.getWitel(), dto.getCode(), dto.getProduct()))
            throw new BadRequestException("isu dengan nama dan product yang sama telah tersedia");

        Issue issue = Issue.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .product(dto.getProduct())
                .description(dto.getDescription())
                .build();

        for (IssueParamDTO param : dto.getParams()) {
            issue.getParams().add(IssueParam.builder()
                    .type(IssueParam.Type.valueOf(param.getType()))
                    .display(param.getDisplay())
                    .required(Boolean.TRUE.equals(param.getRequired()))
                    .build());
        }

        return save(issue);
    }

    @Override
    @Transactional
    public Issue update(String id, UpdateIssueDTO dto) {
        log.info("UPDATING ISSUE -- ID={} NAME={}", id, dto.getName());
        Issue issue = queryService.findById(id);

        BeanUtils.copyProperties(dto, issue, "params", "createdAt", "createdBy", "updatedAt", "updatedBy");

        if (dto.getDeletedParams() != null && !dto.getDeletedParams().isEmpty()) {
            for (Long deletedParam : dto.getDeletedParams()) {
                log.info("DELETE ISSUE PARAM -- {}", deletedParam);
                paramRepo.deleteById(deletedParam);

                for (int i = 0; i < issue.getParams().size(); i++) {
                    IssueParam param = issue.getParams().get(i);
                    if (deletedParam == param.getId()) issue.getParams().remove(param);
                }
            }
        }

        if (dto.getParams() != null && !dto.getParams().isEmpty()) {
            List<IssueParam> params = new ArrayList<>();
            for (IssueParamDTO paramDTO : dto.getParams()) {
                IssueParam param = IssueParam.builder()
                        .type(IssueParam.Type.valueOf(paramDTO.getType()))
                        .display(paramDTO.getDisplay())
                        .required(paramDTO.getRequired())
                        .issue(issue)
                        .build();

                if (paramDTO.getId() != null) {
                    param.setId(paramDTO.getId());
                    paramRepo.findById((paramDTO.getId()))
                            .ifPresent((p) -> {
                                param.setCreatedAt(p.getCreatedAt());
                                param.setCreatedBy(p.getCreatedBy());

                                param.setUpdatedAt(p.getUpdatedAt());
                                param.setUpdatedBy(p.getUpdatedBy());
                            });
                }
                params.add(param);
            }

            issue.setParams(params);
        }

        return save(issue);
    }

    @Override
    public void deleteByIds(Iterable<String> ids) {
        repo.deleteAllById(ids);
    }

    @Override
    public void restoreByIds(Iterable<String> ids) {
        Set<String> a = StreamSupport.stream(ids.spliterator(), true)
                .collect(Collectors.toSet());
        repo.findAllByDeletedIsTrueAndIdIn(a);
    }

}
