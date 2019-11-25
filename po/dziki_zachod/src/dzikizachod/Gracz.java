package dzikizachod;

import java.util.*;

public abstract class Gracz {

    private Strategia strategia;
    private ArrayList<KartaAkcji> mojeKarty = new ArrayList<>();
    private Random mójRand = new Random();
    protected Widok widok;

    Gracz(Strategia strategia) {
        this.strategia = strategia;
    }

    public abstract Gracz dajKopię();

    public abstract void ustawWidok();

    public abstract String rola();

    Strategia strategia() {
        return strategia;
    }

    int zasięg() {
        return widok().zasięg();
    }

    ArrayList<KartaAkcji> mojeKarty() {
        return mojeKarty;
    }

    int ileKart() {
        return mojeKarty.size();
    }

    public Widok widok() {
        return widok;
    }

    void weźKartę(KartaAkcji karta) {
        mojeKarty.add(karta);
    }

    int rzućKostką() {
        return Kostka.rzut(mójRand);
    }

    public void oddajKartę(KartaAkcji karta) {
        mojeKarty.remove(karta);
    }

    public KartaAkcji oddajKartę(int j) {
        return mojeKarty.remove(j);
    }

    public void wyrzućKartę(KartaAkcji karta) {
        //System.out.print("wyrzucam karte: " + karta + " => mojekarty");
        assert(mojeKarty.remove(karta));
        wydrukujKarty();
    }

    void posortujKarty() {
        Collections.sort(mojeKarty, new Comparator<KartaAkcji>() {
            @Override
            public int compare(KartaAkcji karta1, KartaAkcji karta2) {
                if (karta1.priorytet() < karta2.priorytet())
                    return -1;
                if (karta1.priorytet() > karta2.priorytet())
                    return +1;
                return 0;
            }
        });
    }

    Widok.RodzajWidoku rodzaj(Widok widokPytającego) {
        return widok.pokażSwójRodzaj(widokPytającego);
    }

    public Widok.RodzajWidoku zapytajORodzaj(Widok widokGracza) {
        return widokGracza.pokażSwójRodzaj(widok);
    }

    /* dzikizachod.Gracz (strategia gracza) podejmuje decyzję w oparciu o stan gry, kartę aktualnie do zagrania oraz
    resztę kart na ręku do zagrania w aktualnej turze. W tej specyfikacji z reszty kart korzysta tylko sprytna
    strategia bandyty ale ogólnie sensowne wydaje się by strategia mogła korzystać z tej informacji. Zakładam, że
    (ew. dodane w przyszłości) strategie nie będą liczyły kart tzn nie patrzą na historię i dlatego
    nie przekazujemy strategiom historii (poza tej, którą mogą wydobyć z ObrazuGry) */
    Decyzja podejmijDecyzję(ObrazGry obrazGry, KartaAkcji karta) {
        return strategia.jakaDecyzja(obrazGry, kartyPozostałeDoZagrania(mojeKarty, karta), widok);
    }

    /* Z listy kart zwraca podlistę od danej karty (włącznie) do końca */
    private List<KartaAkcji> kartyPozostałeDoZagrania(List<KartaAkcji> karty, KartaAkcji karta) {
        assert(karty.contains(karta));
        return karty.subList(karty.indexOf(karta), karty.size());
    }

    public String toString() {
        return rola() + "(liczba żyć: " + widok.punktyŻycia() + "/"
                + widok.początkowePunktyŻycia() + ", zasięg = " +
        zasięg() + ")";
    }

    public void wydrukujSię() {
        System.out.println(this.toString());
    }

    void wydrukujKarty() {
        System.out.print("[");
        for (KartaAkcji karta : mojeKarty) {
            System.out.print(karta.toString() + ", ");
        }
        System.out.println("]");
    }

    boolean czyŻywy() {
        return widok.czyŻywy();
    }
}
