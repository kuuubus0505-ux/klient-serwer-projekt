package app.model;

import java.io.Serializable;
import java.util.Objects;

public class Ptak implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String gatunek;
    private final boolean potrafiLatac;

    public Ptak(String gatunek, boolean potrafiLatac) {
        this.gatunek = gatunek;
        this.potrafiLatac = potrafiLatac;
    }

    public String getGatunek() { return gatunek; }
    public boolean isPotrafiLatac() { return potrafiLatac; }

    @Override
    public String toString() {
        return "Ptak{gatunek='" + gatunek + "', potrafiLatac=" + potrafiLatac + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ptak)) return false;
        Ptak ptak = (Ptak) o;
        return potrafiLatac == ptak.potrafiLatac && Objects.equals(gatunek, ptak.gatunek);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gatunek, potrafiLatac);
    }
}
