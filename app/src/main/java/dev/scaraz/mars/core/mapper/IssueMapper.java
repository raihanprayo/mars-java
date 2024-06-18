package dev.scaraz.mars.core.mapper;

import dev.scaraz.mars.common.domain.response.IssueDTO;
import dev.scaraz.mars.core.domain.symptom.Issue;
import dev.scaraz.mars.core.domain.order.TcIssue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class IssueMapper {

    public abstract IssueDTO toDTO(Issue issue);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract IssueDTO toDTO(TcIssue issue);

}
