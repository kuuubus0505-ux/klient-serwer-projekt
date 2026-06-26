package app.model;

import java.io.Serializable;
import java.util.Objects;

public class Kot implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String imie;
    private final int wiek;

    public Kot(String imie, int wiek) {
        this.imie = imie;
        this.wiek = wiek;
    }

    public String getImie() { return imie; }
    public int getWiek() { return wiek; }

    @Override
    public String toString() {
        return "Kot{imie='" + imie + "', wiek=" + wiek + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Kot)) return false;
        Kot kot = (Kot) o;
        return wiek == kot.wiek && Objects.equals(imie, kot.imie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imie, wiek);
    }
}
