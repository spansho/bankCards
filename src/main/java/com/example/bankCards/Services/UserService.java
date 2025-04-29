package com.example.bankCards.Services;

import com.example.bankCards.Dto.UserDto;
import com.example.bankCards.Exceptions.UserNotFoundException;
import com.example.bankCards.Models.User;
import com.example.bankCards.Repository.JwtTokenRepository;
import com.example.bankCards.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String createUser (UserDto user) {
        if (findUserByemail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Current email exists: " + user.getEmail());
        }
        User newUser  = User.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .password(passwordEncoder.encode(user.getPassword()))
                .build();
        userRepository.save(newUser);
        return "New User with email " + user.getEmail() + " created";
    }

    public String deleteUser(UserDto userDto)
    {
        var userFromDb=findUserByemail(userDto.getEmail());
        if(userFromDb.isPresent()) {
            userRepository.delete(userFromDb.get());
            return "Пользователь успешно удалён";
        }
        throw new IllegalArgumentException();
    }

    public List<User> getAllUsers()
    {
        return userRepository.findAll();
    }
    public String authenticate(UserDto user) {
        // Используем метод findUser ByEmail для проверки существования пользователя
        Optional<User> existingUser  = findUserByemail(user.getEmail());
        if (existingUser .isPresent()) {
            return jwtTokenRepository.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));
        }
        throw new IllegalArgumentException("Current email does not exist: " + user.getEmail());
    }

    public Optional<User> findUserById(int id) {
        return userRepository.findById(id);
    }

    public Optional<User> findUserByemail(String email) {
        return userRepository.findByemail(email);
    }
}
