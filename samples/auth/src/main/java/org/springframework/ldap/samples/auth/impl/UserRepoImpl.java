package org.springframework.ldap.samples.auth.impl;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.BaseLdapNameAware;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.samples.auth.User;
import org.springframework.ldap.samples.auth.UserRepoExtension;
import org.springframework.ldap.support.LdapUtils;

public class UserRepoImpl implements UserRepoExtension, BaseLdapNameAware {

   @Autowired
   private LdapContextSource contextSource;
   @Autowired
   private final LdapTemplate ldapTemplate;
   private LdapName baseLdapPath;
   

   @Autowired
   public UserRepoImpl(LdapTemplate ldapTemplate) {
      this.ldapTemplate = ldapTemplate;
   }

   @Override
   public void setBaseLdapPath(LdapName baseLdapPath) {
      this.baseLdapPath = baseLdapPath;
   }

   public boolean authenticateUser(String userDn, String credentials) {
      DirContext ctx = null;
      try {
         if (credentials.isEmpty()) {
            // Any userDn with an empty password will log in as anonymous usr
            System.out.println("The password can't be empty.");
            return false;
         }
         ctx = contextSource.getContext(userDn, credentials);
         System.out.println("Logged in successfully.");
         // set the userDn and password of contextSource to be used by ldapTemplate
         contextSource.setUserDn(userDn);
         contextSource.setPassword(credentials);
      } catch (Exception e) {
         // Context creation failed - authentication did not succeed
         System.out.println("Incorrect username or password.");
         System.out.println(e.getMessage());
         return false;
      } finally {
         // It is imperative that the created DirContext instance is always closed
         LdapUtils.closeContext(ctx);
      }
      return true;
   }

   @Override
   public User findUser(String userDn) {
      return ldapTemplate.search(query().where("mail").is(userDn), 
            new PersonAttributesMapper()).get(0);
   }

   private class PersonAttributesMapper implements AttributesMapper<User> {
      public User mapFromAttributes(Attributes attrs) throws NamingException {
         User person = new User();
         try {
            person.setFullName((String)attrs.get("cn").get());
            person.setEmail((String)attrs.get("mail").get());
            person.setEmployeeNumber((String)attrs.get("employeeNumber").get());
            person.setDepartment((String)attrs.get("department").get());
         } catch (NullPointerException e) {
            // not a valid normal user which does not has all the attributes above
         }
         return person;
      }
   }
}
