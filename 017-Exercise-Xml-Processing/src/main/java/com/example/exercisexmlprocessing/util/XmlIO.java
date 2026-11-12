package com.example.exercisexmlprocessing.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlAnyElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes XML lists like {@code <users><user .../><user .../></users>}: the root name is passed in,
 * each item's element name comes from its {@code @XmlRootElement}.
 */
@Component
public class XmlIO {

    /** Generic list root; JAXB fills it with typed items because they are known to the context. */
    static class Items {
        @XmlAnyElement(lax = true)
        List<Object> items = new ArrayList<>();
    }

    private final Path outputDir;

    public XmlIO(@Value("${app.output-dir:output}") String outputDir) {
        this.outputDir = Path.of(outputDir);
    }

    public <T> List<T> readList(String classpathFile, Class<T> itemType) {
        try (InputStream in = new ClassPathResource(classpathFile).getInputStream()) {
            Items root = JAXBContext.newInstance(Items.class, itemType).createUnmarshaller()
                    .unmarshal(new StreamSource(in), Items.class).getValue();
            return root.items.stream().filter(itemType::isInstance).map(itemType::cast).toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (JAXBException e) {
            throw new IllegalStateException("Cannot read " + classpathFile, e);
        }
    }

    /** A list under the given root element. */
    public String toXml(String rootName, List<?> items) {
        Items root = new Items();
        root.items.addAll(items);
        Class<?>[] classes = items.stream().map(Object::getClass).distinct().toArray(Class<?>[]::new);
        Class<?>[] all = new Class<?>[classes.length + 1];
        all[0] = Items.class;
        System.arraycopy(classes, 0, all, 1, classes.length);
        return marshal(new JAXBElement<>(new QName(rootName), Items.class, root), all);
    }

    /** An object whose class has {@code @XmlRootElement}. */
    public String toXml(Object root) {
        return marshal(root, root.getClass());
    }

    public Path write(String fileName, String xml) {
        try {
            Files.createDirectories(outputDir);
            return Files.writeString(outputDir.resolve(fileName), xml);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String marshal(Object root, Class<?>... classes) {
        try {
            Marshaller marshaller = JAXBContext.newInstance(classes).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(root, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }
}
