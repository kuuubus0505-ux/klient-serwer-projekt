package app.model;

import java.io.Serializable;
import java.util.Objects;

public class Pies implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String imie;
    private final String rasa;

    public Pies(String imie, String rasa) {
        this.imie = imie;
        this.rasa = rasa;
    }

    public String getImie() { return imie; }
    public String getRasa() { return rasa; }

    @Override
    public String toString() {
        return "Pies{imie='" + imie + "', rasa='" + rasa + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pies)) return false;
        Pies pies = (Pies) o;
        return Objects.equals(imie, pies.imie) && Objects.equals(rasa, pies.rasa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imie, rasa);
    }
}
