package dev.scaraz.mars.core.v2.query.credential;

import dev.scaraz.mars.core.v2.domain.credential.Account;
import dev.scaraz.mars.core.v2.query.NonSpecificationQueryService;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountQueryService extends
        NonSpecificationQueryService<Account>,
        UserDetailsService {
    Account findById(String id);
}
