package com.example.exerciseSpringDataAutoMappingObjects;

import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserLoginDto;
import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserRegisterDto;
import com.example.exerciseSpringDataAutoMappingObjects.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(OutputCaptureExtension.class)
class CLRTest {

    @Test
    void dispatchesCommandsAndStopsAtEndOfInput(CapturedOutput output) throws Exception {
        UserService userService = mock(UserService.class);
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(("RegisterUser|a@b.bg|Pass123|Pass123|Ann Lee\n"
                                                + "RegisterUser|missing-fields\n"
                                                + "LoginUser|a@b.bg|Pass123\n"
                                                + "Dance\n"
                                                + "Logout\n").getBytes(UTF_8)));
        try {
            new CLR(userService).run();
        } finally {
            System.setIn(originalIn);
        }

        verify(userService).registerUser(argThat((UserRegisterDto dto) -> dto.getFullName().equals("Ann Lee")));
        verify(userService).loginUser(argThat((UserLoginDto dto) -> dto.getEmail().equals("a@b.bg")));
        verify(userService, times(1)).registerUser(any());
        verify(userService).logout();
        assertThat(output.getOut().split("Invalid command", -1)).hasSize(3);
    }
}
