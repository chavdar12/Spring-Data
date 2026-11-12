package com.example.jsonprocessing.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.List;

public class PersonDto {

    @Expose
    private String firstName;

    @Expose
    private String lastName;

    @Expose
    private LocalDate birthDate;

    @Expose
    @SerializedName("married")
    private boolean isMarried;

    @Expose
    private List<String> phoneNumbers;

    @Expose
    private AddressDto address;

    // no @Expose: never written to or read from JSON
    private String password;

    public PersonDto() {
    }

    public PersonDto(String firstName, String lastName, LocalDate birthDate, boolean isMarried, List<String> phoneNumbers,
                     AddressDto address, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.isMarried = isMarried;
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

    public boolean isMarried() {
        return isMarried;
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
