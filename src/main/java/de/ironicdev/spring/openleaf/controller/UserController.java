package de.ironicdev.spring.openleaf.controller;

import de.ironicdev.spring.openleaf.models.User;
import de.ironicdev.spring.openleaf.models.UserRole;
import de.ironicdev.spring.openleaf.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    UserRepository repository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(UserRepository userRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.repository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/users")
    public Iterable<User> getAllUser() {
        return repository.findAll();
    }

    @GetMapping("/users/{userId}")
    public User getSingleUser(@PathVariable("userId") String userId) {
        Optional<User> dbUser = repository.findById(userId);
        return dbUser.orElse(null);
    }

    @PostMapping("/users")
    public User registerNewUser(@RequestBody User user) throws Exception {
        if (user == null)
            throw new Exception("can't save empty user (user = null)");

        // check if required fields are filled
        if (user.getMailAddress() == null || user.getMailAddress().equals(""))
            throw new Exception("mail address cannot be empty");
        if (user.getUsername() == null || user.getUsername().equals(""))
            throw new Exception("username cannot be empty");
        if (user.getPassword() == null || user.getPassword().equals(""))
            throw new Exception("password cannot be empty");

        // check if user id is not self-filled
        if (user.getUserId() != null && user.getUserId().equals(""))
            throw new Exception("can't add User with self-set user id. Please let userId empty");

        // check for already existing username
        if (repository.findByUsername(user.getUsername()).isPresent())
            throw new Exception("username already exists");

        // check for already existing mail address
        if (repository.findByMailAddress(user.getMailAddress()).isPresent())
            throw new Exception("user with mail address already exists");

        // set default user role
        UserRole defaultRole = new UserRole();
        defaultRole.setName("USER");
        user.setMemberRole(defaultRole);

        // set current date as register date
        user.setRegisterDate(new Date());

        // encrypt user password
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        repository.save(user);
        return user;
    }

    @PutMapping("/users")
    public void updateUser(HttpServletResponse response,
                           @RequestBody User updatedUser) throws Exception {
        Optional<User> dbUser = repository.findById(updatedUser.getUserId());

        if (!dbUser.isPresent()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new Exception("no user found to update");
        }

        repository.save(updatedUser);

        // 204 already fine
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(HttpServletResponse response,
                           @PathVariable("userId") String userId) throws Exception {
        Optional<User> dbUser = repository.findById(userId);

        if (dbUser.isPresent()) {
            repository.delete(dbUser.get());
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            throw new Exception("no user found with given user id");
        }
    }
}
