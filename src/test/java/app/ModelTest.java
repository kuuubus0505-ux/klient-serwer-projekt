package app;

import app.model.Kot;
import app.model.Pies;
import app.server.Serwer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void equalsDzialaPoprawnie() {
        assertEquals(new Kot("Mruczek", 3), new Kot("Mruczek", 3));
        assertNotEquals(new Kot("Mruczek", 3), new Kot("Mruczek", 4));
        assertNotEquals(new Kot("Mruczek", 3), new Pies("Mruczek", "kundel"));
    }

    @Test
    void hashCodeSpojnyZEquals() {
        assertEquals(new Pies("Rex", "owczarek").hashCode(),
                     new Pies("Rex", "owczarek").hashCode());
    }

    @Test
    void magazynZawieraPoCzteryObiektyKazdejKlasy() {
        Serwer serwer = new Serwer();
        assertEquals(12, serwer.getMagazyn().size());
        assertTrue(serwer.getMagazyn().containsKey("kot_1"));
        assertTrue(serwer.getMagazyn().containsKey("pies_4"));
    }

    @Test
    void pobierzKolekcjeZwracaWlasciweObiekty() {
        Serwer serwer = new Serwer();
        List<Object> koty = serwer.pobierzKolekcje("kot");
        assertEquals(4, koty.size());
        assertTrue(koty.stream().allMatch(o -> o instanceof Kot));
    }

    @Test
    void pobierzKolekcjeNieznanejKlasyZwracaInnyObiekt() {
        Serwer serwer = new Serwer();
        List<Object> wynik = serwer.pobierzKolekcje("smok");
        assertEquals(1, wynik.size());
        assertNotNull(wynik.get(0));
        // obiekt NIE jest typu odpowiadajacego "smok" (bo taki nie istnieje w magazynie)
        assertFalse(wynik.get(0).getClass().getSimpleName().equalsIgnoreCase("smok"));
    }
}
