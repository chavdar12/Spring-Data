package com.example.xmlprocessing;

import com.example.xmlprocessing.dto.AddressDto;
import com.example.xmlprocessing.dto.PeopleDto;
import com.example.xmlprocessing.dto.PersonDto;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;

/** The lab steps: marshal an object and a list, then unmarshal a string and a file. */
@Component
public class XmlDemo implements CommandLineRunner {

    private final JAXBContext context;

    public XmlDemo() throws JAXBException {
        this.context = JAXBContext.newInstance(PeopleDto.class, PersonDto.class);
    }

    @Override
    public void run(String... args) throws Exception {
        PersonDto pesho = new PersonDto("Pesho", "Peshev", LocalDate.of(1990, 5, 20), List.of("+359 888 123 456"),
                new AddressDto("Bulgaria", "Sofia", "Vitoshka 15"), "secret");

        System.out.println("Object to XML:");
        System.out.println(toXml(pesho));

        System.out.println("List to XML:");
        System.out.println(toXml(new PeopleDto(List.of(pesho))));

        System.out.println("XML to object:");
        PersonDto parsed = fromXml("<person first-name=\"Gosho\" last-name=\"Goshev\"><address country=\"Bulgaria\"><city>Varna</city></address></person>",
                PersonDto.class);
        System.out.println(parsed.getFirstName() + " from " + parsed.getAddress().getCity());

        System.out.println("XML file to objects:");
        for (PersonDto person : readPeople().getPeople()) {
            System.out.printf("%s %s from %s%n", person.getFirstName(), person.getLastName(), person.getAddress().getCity());
        }
    }

    String toXml(Object value) throws JAXBException {
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(value, writer);
        return writer.toString();
    }

    <T> T fromXml(String xml, Class<T> type) throws JAXBException {
        return type.cast(context.createUnmarshaller().unmarshal(new StringReader(xml)));
    }

    PeopleDto readPeople() throws Exception {
        try (InputStream in = new ClassPathResource("people.xml").getInputStream()) {
            return (PeopleDto) context.createUnmarshaller().unmarshal(in);
        }
    }
}
