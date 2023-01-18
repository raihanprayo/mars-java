package dev.scaraz.mars.core.web.rest.admins;

import dev.scaraz.mars.common.domain.response.GroupDTO;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.mapper.GroupMapper;
import dev.scaraz.mars.core.query.GroupQueryService;
import dev.scaraz.mars.core.query.criteria.GroupCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/group")
public class GroupResource {

    private final GroupMapper groupMapper;
    private final GroupQueryService groupQueryService;

    @GetMapping
    public ResponseEntity<?> findAll(GroupCriteria criteria, Pageable pageable) {
        Page<GroupDTO> page = groupQueryService.findAll(criteria, pageable)
                .map(groupMapper::toDTO);
        return ResourceUtil.pagination(page, "/group");
    }

}
