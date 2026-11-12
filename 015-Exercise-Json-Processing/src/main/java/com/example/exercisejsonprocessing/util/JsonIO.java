package com.example.exercisejsonprocessing.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

/** Reads seed files from the classpath and writes query results to the output directory. */
@Component
public class JsonIO {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, ctx) -> LocalDateTime.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (value, type, ctx) -> new JsonPrimitive(value.toString()))
            .create();

    private final Path outputDir;

    public JsonIO(@Value("${app.output-dir:output}") String outputDir) {
        this.outputDir = Path.of(outputDir);
    }

    public <T> T read(String classpathFile, Class<T> type) {
        try (Reader reader = new java.io.InputStreamReader(new ClassPathResource(classpathFile).getInputStream(), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String toJson(Object value) {
        return gson.toJson(value);
    }

    /** Writes the value as JSON to the output directory and returns the file. */
    public Path write(String fileName, Object value) {
        try {
            Files.createDirectories(outputDir);
            return Files.writeString(outputDir.resolve(fileName), toJson(value));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
