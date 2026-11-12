package com.example.jsonprocessing.dto;

import com.google.gson.annotations.Expose;

public class AddressDto {

    @Expose
    private String country;

    @Expose
    private String city;

    @Expose
    private String street;

    public AddressDto() {
    }

    public AddressDto(String country, String city, String street) {
        this.country = country;
        this.city = city;
        this.street = street;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }
}
