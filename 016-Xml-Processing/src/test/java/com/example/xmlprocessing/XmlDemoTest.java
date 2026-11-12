package com.example.xmlprocessing;

import com.example.xmlprocessing.dto.AddressDto;
import com.example.xmlprocessing.dto.PeopleDto;
import com.example.xmlprocessing.dto.PersonDto;
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
class XmlDemoTest {

    @Autowired
    private XmlDemo demo;

    @Test
    void roundTripKeepsMappedFieldsAndDropsTransient() throws Exception {
        PersonDto person = new PersonDto("Pesho", "Peshev", LocalDate.of(1990, 5, 20), List.of("1", "2"),
                new AddressDto("Bulgaria", "Sofia", "Vitoshka 15"), "secret");

        String xml = demo.toXml(person);
        PersonDto back = demo.fromXml(xml, PersonDto.class);

        assertThat(xml).contains("<person first-name=\"Pesho\" last-name=\"Peshev\">", "<birth-date>1990-05-20</birth-date>",
                "<phone-number>2</phone-number>", "<address country=\"Bulgaria\">").doesNotContain("secret");
        assertThat(back.getBirthDate()).isEqualTo(LocalDate.of(1990, 5, 20));
        assertThat(back.getPhoneNumbers()).containsExactly("1", "2");
        assertThat(back.getAddress().getStreet()).isEqualTo("Vitoshka 15");
        assertThat(back.getPassword()).isNull();
    }

    @Test
    void readsPeopleFile() throws Exception {
        PeopleDto people = demo.readPeople();

        assertThat(people.getPeople()).extracting(PersonDto::getFirstName).containsExactly("Ivan", "Maria");
        assertThat(people.getPeople().get(1).getAddress().getCity()).isEqualTo("Burgas");
    }

    @Test
    void demoRuns(CapturedOutput output) throws Exception {
        demo.run();
        assertThat(output).contains("Object to XML:", "<people>", "Gosho from Varna", "Maria Petrova from Burgas");
    }
}
