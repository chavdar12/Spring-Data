package com.example.xmlprocessing.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

/** Root element for a list: JAXB cannot marshal a bare List. */
@XmlRootElement(name = "people")
@XmlAccessorType(XmlAccessType.FIELD)
public class PeopleDto {

    @XmlElement(name = "person")
    private List<PersonDto> people = new ArrayList<>();

    public PeopleDto() {
    }

    public PeopleDto(List<PersonDto> people) {
        this.people = people;
    }

    public List<PersonDto> getPeople() {
        return people;
    }
}
