package org.springframework.ldap.samples.auth.main;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.samples.auth.User;
import org.springframework.ldap.support.LdapUtils;

public class Authentication {

   private static LdapTemplate ldapTemplate;

   public static void main(String[] args) throws Exception {
      String userDn = "email-address";
      String credentials = "your-password";
      LdapContextSource contextSource = new LdapContextSource();
      contextSource.setUrl("ldap://ldap.vmware.com:389");
      contextSource.setBase("OU=Engineering, DC=vmware,DC=com");
      contextSource.setUserDn(userDn);
      contextSource.setPassword(credentials);
      contextSource.afterPropertiesSet();

      DirContext ctx = null;
      try {
         if (credentials.isEmpty()) {
            // Any userDn with an empty password will log in as anonymous usr
            System.out.println("The password can't be empty.");
            return;
         }
         ctx = contextSource.getContext(userDn, credentials);
         System.out.println("Logged in successfully.");
      } catch (Exception e) {
         // Context creation failed - authentication did not succeed
         System.out.println("Incorrect username or password.");
         System.out.println(e.getMessage());
      } finally {
         // It is imperative that the created DirContext instance is always closed
         LdapUtils.closeContext(ctx);
      }

      // get user details
      ldapTemplate = new LdapTemplate(contextSource);
      ldapTemplate.afterPropertiesSet();
      User person = findPerson("name@vmware.com");
      if (person != null) {
         System.out.print(person.getFullName() + " , " + person.getDepartment());
      } else {
         System.out.println("Can not find the user.");
      }
   }

   private static class PersonAttributesMapper implements AttributesMapper<User> {
      public User mapFromAttributes(Attributes attrs) throws NamingException {
         User person = new User();
         try {
            person.setFullName((String)attrs.get("cn").get());
            person.setEmail((String)attrs.get("mail").get());
            person.setEmployeeNumber((String)attrs.get("employeeNumber").get());
            person.setDepartment((String)attrs.get("department").get());
         } catch (NullPointerException e) {
            // not a valid user
         }
         return person;
      }
   }

   public static User findPerson(String username) {
      List<User> users = ldapTemplate.search(query().where("objectclass").is("person")
            .and("mail").is(username), 
            new PersonAttributesMapper());
      return users.isEmpty() ? null : users.get(0);
   }
}
