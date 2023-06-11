package dev.scaraz.mars.user.query;

import dev.scaraz.mars.user.domain.db.MarsUser;
import dev.scaraz.mars.user.web.criteria.UserCriteria;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserQueryService extends UserDetailsService, BaseQueryService<MarsUser, UserCriteria> {
    MarsUser findById(String id);
}
