package com.example.jsonprocessing;

import com.example.jsonprocessing.dto.AddressDto;
import com.example.jsonprocessing.dto.PersonDto;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class JsonDemoTest {

    @Autowired
    private Gson gson;
    @Autowired
    private JsonDemo demo;

    @Test
    void roundTripKeepsExposedFieldsAndDropsPassword() {
        PersonDto person = new PersonDto("Pesho", "Peshev", LocalDate.of(1990, 5, 20), true, List.of("1"),
                new AddressDto("Bulgaria", "Sofia", "Vitoshka 15"), "secret");

        String json = gson.toJson(person);
        PersonDto back = gson.fromJson(json, PersonDto.class);

        assertThat(json).contains("\"birthDate\": \"1990-05-20\"", "\"married\": true").doesNotContain("secret");
        assertThat(back.getBirthDate()).isEqualTo(LocalDate.of(1990, 5, 20));
        assertThat(back.isMarried()).isTrue();
        assertThat(back.getAddress().getCity()).isEqualTo("Sofia");
        assertThat(back.getPassword()).isNull();
    }

    @Test
    void readsPeopleFileIgnoringUnexposedFields() throws Exception {
        PersonDto[] people = demo.readPeople();

        assertThat(people).extracting(PersonDto::getFirstName).containsExactly("Ivan", "Maria");
        assertThat(people[0].getPassword()).isNull();
        assertThat(people[1].isMarried()).isTrue();
    }

    @Test
    void demoRunsAtStartup(CapturedOutput output) throws Exception {
        demo.run();
        assertThat(output).contains("Object to JSON:", "Varna, Primorski 3", "Maria Petrova from Burgas");
    }
}
