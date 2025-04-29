package com.example.bankCards.Controllers;

import com.example.bankCards.Crypto.StringEncryptorConverter;
import com.example.bankCards.Dto.UserDto;
import com.example.bankCards.Models.User;
import com.example.bankCards.Repository.JwtTokenRepository;
import com.example.bankCards.Repository.UserRepository;
import com.example.bankCards.Services.JwtUserDetailsService;
import com.example.bankCards.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@RestController
@RequestMapping("/Entrance")
public class EntranceController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private StringEncryptorConverter stringEncryptorConverter;


    @Autowired
    private JwtTokenRepository jwtTokenRepository;


    @Operation(summary = "Добавляется нового пользователя в бд")
    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody UserDto user)
    {
        try {
            var data=userService.createUser(user);
            return new ResponseEntity<>("New User with email "+user.getEmail()+" created", HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Аутенфицирует пользователя с последюущим возвращением jwt токена")
    @GetMapping("/authentication")
    public ResponseEntity<String> authetication(@RequestBody UserDto user)
    {
        try {
            var data=userService.authenticate(user);
            return new ResponseEntity<>(jwtTokenRepository.generateToken(userDetailsService.loadUserByUsername(user.getEmail())), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @Operation(summary = "Возвращает все пользователй для просмотра администратором")
    @GetMapping("/getUsers")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>> showAllUsers()
    {
        return new ResponseEntity<>(userService.getAllUsers(),HttpStatus.OK);
    }

    @Operation(summary = "Удаляет пользователя")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestBody UserDto userDto)
    {

        try {
            userService.deleteUser(userDto);
            return new ResponseEntity<>("Пользователь был успешно удалён",HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }

}
