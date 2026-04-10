package com.vaultify.service;
import org.springframework.stereotype.Service;

import com.vaultify.model.User;
import com.vaultify.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @SuppressWarnings("null")
    public User register(User user){
        return userRepository.save(user);
    }
    
}
