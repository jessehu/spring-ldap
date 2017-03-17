package org.springframework.ldap.samples.auth.web;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.samples.auth.User;
import org.springframework.ldap.samples.auth.UserRepo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

   @Autowired
   private UserRepo userRepo;

   @RequestMapping(value = "/login", method = POST)
   public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
      StringBuilder msg = new StringBuilder("");
      HttpStatus code;
      boolean ret = userRepo.authenticateUser(username, password);
      if (ret) {
         User user = userRepo.findUser(username);
         msg.append("Logged in as " + user.getFullName() + " successfully.");
         msg.append("\nEmail: " + user.getEmail());
         msg.append("\nEmployee ID: " + user.getEmployeeNumber());
         msg.append("\nDepartment: " + user.getDepartment());
         code = HttpStatus.OK;
      } else {
         msg.append("Incorrect username or password.");
         code = HttpStatus.UNAUTHORIZED;
      }
      System.out.println(msg);
      return new ResponseEntity<String>(msg.toString(), code);
   }

}
