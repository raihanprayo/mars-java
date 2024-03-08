package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.domain.request.CreateIssueDTO;
import dev.scaraz.mars.common.domain.request.UpdateIssueDTO;
import dev.scaraz.mars.common.domain.response.IssueParamDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.utils.CacheConstant;
import dev.scaraz.mars.core.domain.order.Issue;
import dev.scaraz.mars.core.domain.order.IssueParam;
import dev.scaraz.mars.core.query.IssueQueryService;
import dev.scaraz.mars.core.repository.db.order.IssueParamRepo;
import dev.scaraz.mars.core.repository.db.order.IssueRepo;
import dev.scaraz.mars.core.service.order.IssueService;
import dev.scaraz.mars.core.service.order.TicketService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationContext;
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

    private final ApplicationContext applicationContext;

    private final IssueRepo repo;
    private final IssueParamRepo paramRepo;

    private final IssueQueryService queryService;

    @Override
    @CacheEvict(cacheNames = CacheConstant.ISSUES_KEYBOARD)
    public Issue save(Issue issue) {
        return repo.save(issue);
    }

    @Override
    @Transactional
    public Issue create(String name, Product product, @Nullable String description) {
        if (repo.existsByNameAndProduct(name, product))
            throw BadRequestException.duplicateEntity(Issue.class, "name", name);

        log.info("CREATE NEW ISSUE WITH NAME {} AND PRODUCT {}", name, product);
        return save(Issue.builder()
                .name(name.toLowerCase())
                .product(product)
                .description(description)
                .build());
    }

    @Override
    @Transactional
    public Issue create(CreateIssueDTO dto) {
        log.info("CREATE NEW ISSUE {}/{}", dto.getProduct(), dto.getName());

        Issue issue = Issue.builder()
                .name(dto.getName())
                .alias(dto.getCode())
                .product(dto.getProduct())
                .description(dto.getDescription())
                .score(dto.getScore())
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
    public Issue update(long id, UpdateIssueDTO dto) {
        log.info("UPDATING ISSUE -- ID={} NAME={}", id, dto.getName());
        Issue issue = queryService.findById(id)
                .orElseThrow(() -> NotFoundException.entity(Issue.class, "id", id));

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

        issue = save(issue);
        applicationContext.getBean(TicketService.class).updateTicketIssue(issue);
        return issue;
    }

    @Override
    public void deleteByIds(Iterable<Long> ids) {
        repo.deleteAllById(ids);
    }

    @Override
    public void restoreByIds(Iterable<Long> ids) {
        Set<Long> a = StreamSupport.stream(ids.spliterator(), true)
                .collect(Collectors.toSet());
        repo.findAllByDeletedIsTrueAndIdIn(a);
    }

}
