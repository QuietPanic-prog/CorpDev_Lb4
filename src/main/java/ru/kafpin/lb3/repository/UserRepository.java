package ru.kafpin.lb3.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import ru.kafpin.lb3.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
