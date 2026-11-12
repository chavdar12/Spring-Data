package com.example.jsonprocessing;

import com.example.jsonprocessing.dto.AddressDto;
import com.example.jsonprocessing.dto.PersonDto;
import com.google.gson.Gson;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

/** The lab steps: serialize an object and a list, then deserialize a string and a file. */
@Component
public class JsonDemo implements CommandLineRunner {

    private final Gson gson;

    public JsonDemo(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void run(String... args) throws Exception {
        PersonDto pesho = new PersonDto("Pesho", "Peshev", LocalDate.of(1990, 5, 20), true, List.of("+359 888 123 456"),
                new AddressDto("Bulgaria", "Sofia", "Vitoshka 15"), "secret");

        System.out.println("Object to JSON:");
        System.out.println(gson.toJson(pesho));

        System.out.println("List to JSON:");
        System.out.println(gson.toJson(List.of(pesho.getAddress(), new AddressDto("Bulgaria", "Plovdiv", "Glavna 1"))));

        System.out.println("JSON to object:");
        AddressDto address = gson.fromJson("{\"country\":\"Bulgaria\",\"city\":\"Varna\",\"street\":\"Primorski 3\"}", AddressDto.class);
        System.out.println(address.getCity() + ", " + address.getStreet());

        System.out.println("JSON file to array:");
        for (PersonDto person : readPeople()) {
            System.out.printf("%s %s from %s%n", person.getFirstName(), person.getLastName(), person.getAddress().getCity());
        }
    }

    PersonDto[] readPeople() throws Exception {
        try (Reader reader = new InputStreamReader(new ClassPathResource("people.json").getInputStream(), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, PersonDto[].class);
        }
    }
}
