package org.springframework.ldap.samples.auth;

import org.springframework.ldap.repository.LdapRepository;

public interface UserRepo extends LdapRepository<User>, UserRepoExtension {
}
