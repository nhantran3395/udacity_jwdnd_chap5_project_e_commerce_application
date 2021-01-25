package com.example.demo.service;

import com.example.demo.model.mapper.CreateUserRequestToUserEntityMapper;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

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
        user.setCreatedAt(LocalDateTime.now());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);

        user = userRepository.save(user);

        return user;
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username).orElse(null);
    }

    public User findById(long id){
        return userRepository.findById(id).orElse(null);
    }

}
