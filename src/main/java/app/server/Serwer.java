package app.server;

import app.common.Protokol;
import app.model.Kot;
import app.model.Pies;
import app.model.Ptak;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Serwer {

    public static final int MAX_CLIENTS = 3;
    public static final int PORT = 5000;

    private final Map<String, Object> magazyn = new HashMap<>();
    private final AtomicInteger aktywniKlienci = new AtomicInteger(0);
    private final Random random = new Random();
    private volatile boolean dziala = true;
    private ServerSocket serverSocket;

    public Serwer() {
        inicjalizujDane();
    }

    /** Tworzy po 4 obiekty kazdej klasy i umieszcza je w mapie z kluczami typu kot_1. */
    private void inicjalizujDane() {
        String[] koty = {"Mruczek", "Filemon", "Garfield", "Tom"};
        for (int i = 0; i < 4; i++) {
            magazyn.put("kot_" + (i + 1), new Kot(koty[i], i + 1));
        }
        String[][] psy = {{"Rex", "owczarek"}, {"Burek", "kundel"},
                          {"Azor", "labrador"}, {"Reksio", "jamnik"}};
        for (int i = 0; i < 4; i++) {
            magazyn.put("pies_" + (i + 1), new Pies(psy[i][0], psy[i][1]));
        }
        String[] ptaki = {"wrobel", "orzel", "strus", "kolibri"};
        boolean[] lata = {true, true, false, true};
        for (int i = 0; i < 4; i++) {
            magazyn.put("ptak_" + (i + 1), new Ptak(ptaki[i], lata[i]));
        }
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("[SERWER] Nasluchuje na porcie " + PORT + " (MAX_CLIENTS=" + MAX_CLIENTS + ")");
        while (dziala) {
            try {
                Socket socket = serverSocket.accept();
                // kazdy klient w osobnym watku
                new Thread(() -> obsluzKlienta(socket)).start();
            } catch (IOException e) {
                if (dziala) System.out.println("[SERWER] Blad accept: " + e.getMessage());
            }
        }
    }

    public void stop() throws IOException {
        dziala = false;
        if (serverSocket != null) serverSocket.close();
    }

    private void obsluzKlienta(Socket socket) {
        try (Socket s = socket;
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {

            int id = in.readInt();

            // bezpieczne sprawdzenie limitu
            if (aktywniKlienci.incrementAndGet() > MAX_CLIENTS) {
                aktywniKlienci.decrementAndGet();
                out.writeObject(Protokol.REFUSED);
                out.flush();
                System.out.println("[SERWER] ODRZUCONO klienta ID=" + id + " (przekroczono MAX_CLIENTS)");
                return;
            }

            try {
                out.writeObject(Protokol.OK);
                out.flush();
                System.out.println("[SERWER] PRZYJETO klienta ID=" + id
                        + " (aktywni: " + aktywniKlienci.get() + ")");

                // petla obslugi zadan klienta
                while (true) {
                    Object zadanie;
                    try {
                        zadanie = in.readObject();
                    } catch (EOFException eof) {
                        break; // klient sie rozlaczyl
                    }
                    if (zadanie == null || "KONIEC".equals(zadanie)) break;

                    String nazwaKlasy = zadanie.toString();
                    // losowe opoznienie symulujace obciazenie
                    Thread.sleep(100 + random.nextInt(400));

                    List<Object> kolekcja = pobierzKolekcje(nazwaKlasy);
                    out.writeObject(kolekcja);
                    out.flush();
                    out.reset();

                    System.out.println("[SERWER] Wyslalem do ID=" + id
                            + " (klasa '" + nazwaKlasy + "'): " + kolekcja);
                }
            } finally {
                aktywniKlienci.decrementAndGet();
                System.out.println("[SERWER] Zakonczono obsluge ID=" + id
                        + " (aktywni: " + aktywniKlienci.get() + ")");
            }
        } catch (Exception e) {
            System.out.println("[SERWER] Wyjatek obslugi klienta: " + e.getMessage());
        }
    }

    /**
     * Pobiera z mapy wszystkie obiekty danej klasy (np. "kot").
     * Jesli klasy nie ma w magazynie, celowo zwraca obiekt innej klasy,
     * by wymusic blad rzutowania po stronie klienta.
     */
    List<Object> pobierzKolekcje(String nazwaKlasy) {
        String prefix = nazwaKlasy.toLowerCase() + "_";
        List<Object> wynik = magazyn.entrySet().stream()
                .filter(e -> e.getKey().startsWith(prefix))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (wynik.isEmpty()) {
            // celowo "zly" obiekt - pierwszy dostepny z magazynu
            Object inny = magazyn.values().iterator().next();
            wynik = new ArrayList<>();
            wynik.add(inny);
        }
        return wynik;
    }

    // gettery do testow jednostkowych (bez warstwy sieciowej)
    Map<String, Object> getMagazyn() { return magazyn; }

    public static void main(String[] args) throws IOException {
        new Serwer().start();
    }
}
