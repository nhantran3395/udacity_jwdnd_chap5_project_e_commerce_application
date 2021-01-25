package com.example.demo.service;

import com.example.demo.model.mapper.CreateUserRequestToUserEntityMapper;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreateUserRequestToUserEntityMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User create(CreateUserRequest request){
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ValidationException("Username exists!");
        }
        if (!request.getPassword().equals(request.getRePassword())) {
            throw new ValidationException("Passwords don't match!");
        }

        User user = mapper.create(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);

        return user;
    }
}
