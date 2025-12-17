package com.marketplace.marketplaceauthservice.repository;

import com.marketplace.marketplaceauthservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    boolean existsUserByUsername(String username);

    Optional<Object> findByEmail(String email);
}
