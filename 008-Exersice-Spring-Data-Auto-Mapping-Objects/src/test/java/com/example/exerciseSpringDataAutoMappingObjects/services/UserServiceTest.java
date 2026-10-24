package com.example.exerciseSpringDataAutoMappingObjects.services;

import com.example.exerciseSpringDataAutoMappingObjects.config.ApplicationBeanConfiguration;
import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserLoginDto;
import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserRegisterDto;
import com.example.exerciseSpringDataAutoMappingObjects.repositories.UserRepository;
import com.example.exerciseSpringDataAutoMappingObjects.services.impl.UserServiceImpl;
import com.example.exerciseSpringDataAutoMappingObjects.utilities.ValidationUtilImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(OutputCaptureExtension.class)
@Import({UserServiceImpl.class, ValidationUtilImpl.class, ApplicationBeanConfiguration.class})
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private void registerPesho() {
        userService.registerUser(new UserRegisterDto("pesho@abv.bg", "Pass123", "Pass123", "Pesho Peshev"));
    }

    @Test
    void registersValidUser(CapturedOutput output) {
        registerPesho();

        assertThat(userRepository.findAll()).singleElement().satisfies(user -> {
            assertThat(user.getEmail()).isEqualTo("pesho@abv.bg");
            assertThat(user.getFullName()).isEqualTo("Pesho Peshev");
        });
        assertThat(output).contains("Pesho Peshev was registered");
    }

    @Test
    void rejectsMismatchedConfirmPassword(CapturedOutput output) {
        userService.registerUser(new UserRegisterDto("pesho@abv.bg", "Pass123", "Pass124", "Pesho"));

        assertThat(userRepository.count()).isZero();
        assertThat(output).contains("Wrong confirm password");
    }

    @Test
    void rejectsInvalidEmail(CapturedOutput output) {
        userService.registerUser(new UserRegisterDto("not-an-email", "Pass123", "Pass123", "Pesho"));

        assertThat(userRepository.count()).isZero();
        assertThat(output).contains("Enter valid email");
    }

    @Test
    void rejectsDuplicateEmail(CapturedOutput output) {
        registerPesho();
        registerPesho();

        assertThat(userRepository.count()).isOne();
        assertThat(output).contains("Email already registered");
    }

    @Test
    void loginAndLogout(CapturedOutput output) {
        registerPesho();

        userService.loginUser(new UserLoginDto("pesho@abv.bg", "Pass123"));
        userService.logout();
        userService.logout();

        assertThat(output).contains("Successfully logged in Pesho Peshev", "User Pesho Peshev successfully logged out",
                "Cannot logout. No user was logged in");
    }

    @Test
    void loginWithWrongPasswordFails(CapturedOutput output) {
        registerPesho();

        userService.loginUser(new UserLoginDto("pesho@abv.bg", "Wrong123"));

        assertThat(output).contains("Incorrect username/password").doesNotContain("Successfully logged in");
    }
}
