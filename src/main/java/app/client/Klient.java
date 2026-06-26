package app.client;

import app.common.Protokol;
import app.model.Kot;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Klient {

    private final int id;
    private final String host;
    private final int port;

    public Klient(int id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

    public void uruchom() {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeInt(id);
            out.flush();

            String status = (String) in.readObject();
            if (Protokol.REFUSED.equals(status)) {
                System.out.println("[KLIENT " + id + "] Odmowa obslugi (REFUSED). Koncze.");
                return;
            }
            System.out.println("[KLIENT " + id + "] Polaczono (OK).");

            // kilka zapytan o rozne klasy; "smok" nie istnieje -> blad rzutowania
            String[] klasy = {"kot", "pies", "ptak", "smok"};
            for (String klasa : klasy) {
                out.writeObject(klasa);
                out.flush();
                out.reset();

                @SuppressWarnings("unchecked")
                List<Object> kolekcja = (List<Object>) in.readObject();
                przetworz(klasa, kolekcja);
            }

            out.writeObject("KONIEC");
            out.flush();
        } catch (Exception e) {
            System.out.println("[KLIENT " + id + "] Blad: " + e.getMessage());
        }
    }

    /** Przetwarza kolekcje strumieniowo; obsluguje blad rzutowania. */
    private void przetworz(String klasa, List<Object> kolekcja) {
        System.out.println("[KLIENT " + id + "] Odebrano kolekcje dla '" + klasa + "':");
        kolekcja.forEach(obj -> {
            try {
                // dla nieznanej klasy "smok" serwer odsyla obiekt innej klasy,
                // co wymusza ClassCastException przy rzutowaniu
                if ("smok".equals(klasa)) {
                    Kot k = (Kot) obj;
                    System.out.println("   " + k);
                } else {
                    System.out.println("   " + obj);
                }
            } catch (ClassCastException ex) {
                System.out.println("[KLIENT " + id + "] BLAD RZUTOWANIA dla '"
                        + klasa + "': otrzymano " + obj.getClass().getSimpleName()
                        + " (" + obj + ")");
            }
        });
    }

    public static void main(String[] args) {
        int id = args.length > 0 ? Integer.parseInt(args[0]) : 1;
        new Klient(id, "localhost", 5000).uruchom();
    }
}
