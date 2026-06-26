package app;

import app.model.Kot;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SerializacjaTest {

    @Test
    void serializacjaIDeserializacjaKolekcji() throws Exception {
        List<Object> oryginal = Arrays.asList(new Kot("A", 1), new Kot("B", 2));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(oryginal);
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
            @SuppressWarnings("unchecked")
            List<Object> odczyt = (List<Object>) ois.readObject();
            assertEquals(oryginal, odczyt);
        }
    }

    @Test
    void bledneRzutowaniePoDeserializacji() throws Exception {
        Object obj = new app.model.Pies("Rex", "owczarek");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
        }
        try (ObjectInputStream ois =
                     new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
            Object odczyt = ois.readObject();
            assertThrows(ClassCastException.class, () -> {
                Kot k = (Kot) odczyt;
                k.getImie();
            });
        }
    }
}
