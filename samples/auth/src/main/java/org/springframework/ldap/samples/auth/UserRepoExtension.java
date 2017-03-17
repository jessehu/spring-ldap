package org.springframework.ldap.samples.auth;

public interface UserRepoExtension {
   // The userDn supplied to the authenticate method needs to be the full DN of the user to authenticate (regardless of the base setting on the ContextSource).
   boolean authenticateUser(String userDn, String credentials);
   User findUser(String userDn);
}
