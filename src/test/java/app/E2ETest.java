package app;

import app.client.Klient;
import app.common.Protokol;
import app.server.Serwer;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class E2ETest {

    static Serwer serwer;
    static Thread serwerThread;

    @BeforeAll
    static void startServer() throws Exception {
        serwer = new Serwer();
        serwerThread = new Thread(() -> {
            try { serwer.start(); } catch (IOException ignored) {}
        });
        serwerThread.setDaemon(true);
        serwerThread.start();
        Thread.sleep(500);
    }

    @AfterAll
    static void stopServer() throws Exception {
        serwer.stop();
    }

    @Test
    @Order(1)
    void klientOtrzymujeOK() throws Exception {
        try (Socket s = new Socket("localhost", Serwer.PORT);
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {
            out.writeInt(100);
            out.flush();
            assertEquals(Protokol.OK, in.readObject());
            out.writeObject("KONIEC");
            out.flush();
        }
    }

    @Test
    @Order(2)
    void nadmiarowyKlientOtrzymujeREFUSED() throws Exception {
        // otwarcie MAX_CLIENTS trwalych polaczen
        Socket[] sockets = new Socket[Serwer.MAX_CLIENTS];
        ObjectOutputStream[] outs = new ObjectOutputStream[Serwer.MAX_CLIENTS];
        for (int i = 0; i < Serwer.MAX_CLIENTS; i++) {
            sockets[i] = new Socket("localhost", Serwer.PORT);
            outs[i] = new ObjectOutputStream(sockets[i].getOutputStream());
            ObjectInputStream in = new ObjectInputStream(sockets[i].getInputStream());
            outs[i].writeInt(i);
            outs[i].flush();
            assertEquals(Protokol.OK, in.readObject());
        }

        // klient nadmiarowy
        try (Socket s = new Socket("localhost", Serwer.PORT);
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {
            out.writeInt(999);
            out.flush();
            assertEquals(Protokol.REFUSED, in.readObject());
        }

        // sprzatanie
        for (int i = 0; i < Serwer.MAX_CLIENTS; i++) {
            outs[i].writeObject("KONIEC");
            outs[i].flush();
            sockets[i].close();
        }
        Thread.sleep(300);
    }

    @Test
    @Order(3)
    void obslugaBleduRzutowania() {
        // pelny scenariusz klienta - nie rzuca wyjatku na zewnatrz,
        // bo ClassCastException jest obsluzony wewnatrz klienta
        assertDoesNotThrow(() -> new Klient(50, "localhost", Serwer.PORT).uruchom());
    }
}
