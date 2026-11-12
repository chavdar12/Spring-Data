package com.example.exercisexmlprocessing.util;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    @Override
    public LocalDateTime unmarshal(String value) {
        return LocalDateTime.parse(value.trim());
    }

    @Override
    public String marshal(LocalDateTime value) {
        return value.toString();
    }
}
