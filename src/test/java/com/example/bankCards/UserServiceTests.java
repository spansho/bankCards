package com.example.bankCards;

import com.example.bankCards.Dto.UserDto;
import com.example.bankCards.Generators.CardNumberGenerator;
import com.example.bankCards.Models.User;
import com.example.bankCards.Repository.BankCardsRepository;
import com.example.bankCards.Repository.UserRepository;
import com.example.bankCards.Services.BankCardService;
import com.example.bankCards.Services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")

public class UserServiceTests {
    @Autowired
    private UserRepository userRepository;


    @MockitoBean
    private CardNumberGenerator cardNumberGenerator;

    @Autowired
    private UserService userService;

        @Test
        @Transactional
        public void testCreateUser () {
            UserDto userDto = new UserDto();
            userDto.setEmail("test@example.com");
            userDto.setPassword("password");
            userDto.setRole("USER");

            String response = userService.createUser (userDto);
            assertThat(response).contains("New User with email test@example.com created");

            Optional<User> user = userRepository.findByemail("test@example.com");
            assertThat(user).isPresent();
            assertThat(user.get().getEmail()).isEqualTo("test@example.com");
        }

        @Test
        public void testCreateUserWithExistingEmail() {
            UserDto userDto1 = new UserDto();
            userDto1.setEmail("test@example.com");
            userDto1.setPassword("password");
            userDto1.setRole("USER");

            userService.createUser (userDto1); // Создаем первого пользователя

            UserDto userDto2 = new UserDto();
            userDto2.setEmail("test@example.com"); // Тот же email
            userDto2.setPassword("newpassword");
            userDto2.setRole("USER");

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.createUser (userDto2); // Пытаемся создать второго пользователя с тем же email
            });

            assertThat(exception.getMessage()).isEqualTo("Current email exists: test@example.com");

        }

        @Test
        @Transactional
        public void testAuthenticateUser () {
            UserDto userDto = new UserDto();
            userDto.setEmail("test@example.com");
            userDto.setPassword("password");
            userDto.setRole("USER");

            userService.createUser (userDto); // Сначала создаем пользователя

            String token = userService.authenticate(userDto);
            assertThat(token).isNotNull(); // Проверяем, что токен не null
        }

        @Test
        public void testAuthenticateUserWithNonExistingEmail() {
            UserDto userDto = new UserDto();
            userDto.setEmail("nonexistent@example.com");
            userDto.setPassword("password");
            userDto.setRole("USER");

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.authenticate(userDto); // Пытаемся аутентифицировать несуществующего пользователя
            });

            assertThat(exception.getMessage()).isEqualTo("Current email does not exist: nonexistent@example.com");
        }

        @Test
        @Transactional
        public void testFindUserById() {
            UserDto userDto = new UserDto();
            userDto.setEmail("test@example.com");
            userDto.setPassword("password");
            userDto.setRole("USER");


            userService.createUser (userDto);
            var userFromDb=userService.findUserByemail("test@example.com");
            Optional<User> user = userService.findUserById(userFromDb.get().getId());
            assertThat(user.isPresent());
        }

        @Test
        @Transactional
        public void testFindUserByEmailShouldBeTrue() {
            UserDto userDto = new UserDto();
            userDto.setEmail("test@example.com");
            userDto.setPassword("password");
            userDto.setRole("USER");

            userService.createUser (userDto); // Создаем пользователя

            Optional<User> user = userService.findUserByemail("test@example.com");
            assertThat(user.isPresent());
        }

        @Test
        public void testFindUserByEmailNotFoundShouldBeFalse() {

            Optional<User> user = userService.findUserByemail("nonexistent@examplezx.com");
            assertThat(user.isEmpty()); // Проверяем, что пользователь не найден
        }
}
