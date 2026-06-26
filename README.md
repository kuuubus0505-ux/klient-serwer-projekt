# Aplikacja klient-serwer (Java)

Prosta aplikacja w modelu klient-serwer. Serwer przechowuje obiekty trzech klas
(Kot, Pies, Ptak) w mapie, obsluguje wielu klientow wielowatkowo z limitem
MAX_CLIENTS i serializuje kolekcje przez gniazda sieciowe. Klient przetwarza
odebrane dane strumieniowo (Stream API) oraz obsluguje celowy blad rzutowania.

## Sklad zespolu i odpowiedzialnosci

> UZUPELNIJ ponizsze dane przed oddaniem projektu.

- Imie Nazwisko - modele danych, serwer
- Imie Nazwisko - klient, protokol komunikacji
- Imie Nazwisko - testy jednostkowe i integracyjne
- Imie Nazwisko - testy E2E, dokumentacja

## Wymagania

- Java 17 lub nowsza
- Maven 3.8 lub nowszy

## Struktura projektu

```
src/
  main/java/app/
    model/    -> Kot.java, Pies.java, Ptak.java
    common/   -> Protokol.java
    server/   -> Serwer.java
    client/   -> Klient.java
  test/java/app/
    ModelTest.java         (testy jednostkowe)
    SerializacjaTest.java  (testy integracyjne)
    E2ETest.java           (testy akceptacyjne / E2E)
pom.xml
```

## Kompilacja

```
mvn clean compile
```

## Uruchomienie serwera

```
mvn exec:java -Dexec.mainClass=app.server.Serwer
```

lub po zbudowaniu:

```
java -cp target/classes app.server.Serwer
```

## Podlaczenie klienta (w osobnym terminalu)

```
java -cp target/classes app.client.Klient 1
```

Argument to ID klienta. Uruchom kilku klientow (1, 2, 3, 4...), aby zobaczyc
przyjecia (OK) oraz odrzucenie (REFUSED) po przekroczeniu MAX_CLIENTS (= 3).

## Uruchomienie testow

```
mvn test
```

## Jak dziala protokol komunikacji

1. Klient laczy sie z serwerem i wysyla swoje ID (liczba int).
2. Serwer odpowiada `OK` lub `REFUSED` (przy przekroczeniu MAX_CLIENTS) i
   wypisuje informacje na konsoli.
3. Przy `REFUSED` klient konczy dzialanie.
4. Przy `OK` klient prosi o kolekcje obiektow danej klasy (np. "kot").
5. Serwer pobiera obiekty z mapy, serializuje liste i odsyla ja klientowi,
   wypisujac na konsoli co i komu wyslal.
6. Dla nieistniejacej klasy ("smok") serwer celowo odsyla obiekt innej klasy,
   przez co klient musi obsluzyc blad rzutowania (ClassCastException).
7. Klient powtarza zapytania dla kilku klas i konczy slowem "KONIEC".

## Deklaracja uzycia sztucznej inteligencji

- **Narzedzie / model:** Claude (Anthropic).
- **Zakres wykorzystania:** wygenerowanie szkieletu projektu (modele danych,
  serwer, klient), implementacja protokolu OK/REFUSED i logiki wielowatkowej,
  testy jednostkowe, integracyjne i E2E oraz niniejsza dokumentacja README.
- **Przykladowe (kluczowe) prompty:**
  - "Napisz aplikacje klient-serwer w Javie: 3 klasy Serializable, serwer
    z mapa obiektow (klucze kot_1, kot_2...), stala MAX_CLIENTS, watek na
    klienta, losowe opoznienia, protokol OK/REFUSED oraz celowy blad
    rzutowania po stronie klienta."
  - "Dodaj testy JUnit 5: jednostkowe (equals oraz pobieranie obiektow
    z mapy), integracyjne (serializacja/deserializacja przez ByteArrayStream)
    oraz E2E (N klientow <= MAX_CLIENTS, odrzucenie nadmiarowego klienta,
    obsluga ClassCastException)."
- **Parametry konfiguracyjne modelu:** ustawienia domyslne, bez dodatkowej
  konfiguracji.

## Kryteria projektu - mapa pokrycia

- Komunikacja sieciowa: `Serwer.java`, `Klient.java`, `Protokol.java`.
- Wielowatkowosc i limit MAX_CLIENTS: `Serwer.java` (watek na klienta,
  AtomicInteger, losowe opoznienia).
- Serializacja i Stream API: `ObjectOutputStream`/`ObjectInputStream`,
  `Stream` w `pobierzKolekcje` oraz `forEach` w kliencie.
- Obsluga bledow i polimorfizm: celowy ClassCastException w `Klient.java`.
- Testy: `ModelTest`, `SerializacjaTest`, `E2ETest`.
