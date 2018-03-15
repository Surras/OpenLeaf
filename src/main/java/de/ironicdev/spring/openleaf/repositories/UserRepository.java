package de.ironicdev.spring.openleaf.repositories;

import de.ironicdev.spring.openleaf.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByMailAddress(String mailAddress);
}
