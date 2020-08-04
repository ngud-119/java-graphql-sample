package com.example.jgs.service;

import com.example.jgs.input.CreateUser;
import com.example.jgs.model.User;
import com.example.jgs.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> find() {
        return this.userRepository.findAll();
    }

    public User findOneById(UUID id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public User create(CreateUser createUser) {
        return this.userRepository.save(User.from(createUser));
    }

    public boolean delete(UUID id) {
        if (this.userRepository.existsById(id)) {
            this.userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
