package ru.amin.Rest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.UserRepository;
import ru.amin.Rest.security.UsersDetails;

import java.util.Optional;

@Component
public class UsersDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public UsersDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<Users> users = userRepository.findByName(s);

        if (users.isEmpty())
            throw new UsernameNotFoundException("User not found");

        
        return new UsersDetails(users.get());
    }
}
