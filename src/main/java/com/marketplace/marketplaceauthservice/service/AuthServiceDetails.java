package com.marketplace.marketplaceauthservice.service;

import com.marketplace.marketplaceauthservice.model.MyUserDetails;
import com.marketplace.marketplaceauthservice.model.User;
import com.marketplace.marketplaceauthservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceDetails implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthServiceDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // MyUserDetails istifadə olunur
        return new MyUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getRole().name() // "ROLE_USER" və ya "ROLE_ADMIN"
        );
    }
}
