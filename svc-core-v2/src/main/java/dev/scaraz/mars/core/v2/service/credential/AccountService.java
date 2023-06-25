package dev.scaraz.mars.core.v2.service.credential;

import dev.scaraz.mars.core.v2.domain.credential.Account;
import dev.scaraz.mars.core.v2.domain.credential.AccountCredential;
import dev.scaraz.mars.core.v2.domain.credential.AccountExpired;

public interface AccountService {
    Account save(Account a);

    AccountCredential save(AccountCredential a);

    AccountExpired save(AccountExpired a);
}
