package com.example.xmlprocessing.dto;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDate;
import java.util.List;

@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonDto {

    @XmlAttribute(name = "first-name")
    private String firstName;

    @XmlAttribute(name = "last-name")
    private String lastName;

    @XmlElement(name = "birth-date")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate birthDate;

    @XmlElementWrapper(name = "phone-numbers")
    @XmlElement(name = "phone-number")
    private List<String> phoneNumbers;

    @XmlElement
    private AddressDto address;

    // never written to or read from XML
    @XmlTransient
    private String password;

    public PersonDto() {
    }

    public PersonDto(String firstName, String lastName, LocalDate birthDate, List<String> phoneNumbers, AddressDto address,
                     String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phoneNumbers = phoneNumbers;
        this.address = address;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public AddressDto getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }
}
