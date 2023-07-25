package dev.scaraz.mars.common.utils;

public interface AuthorityConstant {
    String ADMIN_ROLE = "admin";
    String AGENT_ROLE = "user_agent";
    String USER_ROLE = "user";

    String ADMIN_NIK = "000001413914";


    String HAS_ROLE_ADMIN = "hasRole('" + ADMIN_ROLE + "')";
    String HAS_ROLE_AGENT = "hasRole('" + AGENT_ROLE + "')";
}
