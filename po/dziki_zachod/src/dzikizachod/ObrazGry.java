package dzikizachod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ObrazGry {
    private ArrayList<Widok> widokiAktywnychGraczy = new ArrayList<>();
    private HashMap<Widok, Widok> zabiciGracze = new HashMap<>();
    private Widok szeryf;
    private Dynamit dynamit;
    private static Widok widokGry = new WidokGry();

    public ObrazGry(ArrayList<Gracz> gracze) {
        for (Gracz gracz : gracze) {
            Widok widok = gracz.widok();
            widokiAktywnychGraczy.add(widok);
            if (widok.czyToSzeryf())
                szeryf = widok;
        }
        dynamit = new Dynamit();
    }

    ArrayList<Widok> widokiAktywnychGraczy() {
        return widokiAktywnychGraczy;
    }

    private Widok przedKimLeżyDynamit() {
        return dynamit.przedKimLeży();
    }

    private boolean czyDynamitAktywny() {
        return dynamit.czyAktywny();
    }

    void przestawDynamit() {
        assert(czyDynamitAktywny());
        dynamit.połóżPrzed(nastepnyWidokOd(przedKimLeżyDynamit()));
    }

    Widok dajSzeryfa() {
        return szeryf;
    }

    int indeksWidoku(Widok widok) {
        return widokiAktywnychGraczy().indexOf(widok);
    }

    /* jesli zabił dynamit to ktoZabił == null */
    void graczZabityPrzez(Widok graczZabity, Widok ktoZabił) {
        //System.out.println("[OBRAZ]: gracz" + graczZabity.toString() +" zabity przez " + ktoZabił);
        assert(widokiAktywnychGraczy.contains(graczZabity));
        widokiAktywnychGraczy.remove(graczZabity);
        zabiciGracze.put(graczZabity, ktoZabił);
    }

    void padłStrzał(Widok odKogo, Widok doKogo) {
        doKogo.odejmijPunktyŻycia(1);
        doKogo.padłStrzał(odKogo);
        if (!doKogo.czyŻywy()) {
            graczZabityPrzez(doKogo, odKogo);
        }
    }

    void wybuchłDynamitPrzed(Widok rażonyDynamitem) {
        dynamit.połóżPrzed(null);
        rażonyDynamitem.odejmijPunktyŻycia(Gra.SIŁA_RAŻENIA_DYNAMITU);
        if (!rażonyDynamitem.czyŻywy()) {
            graczZabityPrzez(rażonyDynamitem, null);
        }
    }

    private int liczbaZabitychPomocnikówMinusZabójców(Widok widok) {
        int res = 0;
        for (Widok zabity : zabiciGracze.keySet()) {
            if (zabiciGracze.get(zabity) == widok) {
                if (zabity.pokażSwójRodzaj(widok) == Widok.RodzajWidoku.POMOCNIK_SZERYFA)
                    res++;
                else
                    res--; /* szeryfa nie może być wsród zabitych */
            }
        }

        return res;
    }

    private List<Widok> zabiliWięcejPomocnikówNiżBandytów(List<Widok> lista) {
        List<Widok> res = new ArrayList<>();

        for (Widok podejrzany : lista) {
            if (liczbaZabitychPomocnikówMinusZabójców(podejrzany) > 0)
                res.add(podejrzany);
        }

        return res;
    }

    boolean czySzeryfŻyje() {
        return szeryf.czyŻywy();
    }

    boolean czyBandyciŻyją() {
        for (Widok widok : widokiAktywnychGraczy) {
            if (widok.pokażSwójRodzaj(widokGry) == Widok.RodzajWidoku.BANDYTA)
                return true;
        }
        return false;
    }

    Gra.StanGry sprawdźStanGry() {
        if (!czySzeryfŻyje())
            return Gra.StanGry.WYGRALI_BANDYCI;
        if (!czyBandyciŻyją())
            return Gra.StanGry.WYGRAŁ_SZERYF;

        return Gra.StanGry.GRAMY;
    }

    List<Widok> ktoWZasięgu(Widok widok) {
        return Pomocnicza.wZasięgu(widokiAktywnychGraczy(), indeksWidoku(widok), widok.zasięg());
    }

    List<Widok> ktoWZasięguDoPrzodu(Widok widok) {
        return Pomocnicza.wZasięguDoPrzodu(widokiAktywnychGraczy(), indeksWidoku(widok), widok.zasięg());
    }

    Widok czySzeryfWZasięgu(Widok widok) {
        List<Widok> wZasiegu = ktoWZasięgu(widok);
        for (Widok kto : wZasiegu) {
            if (kto.czyToSzeryf())
                return kto;
        }

        return null;
    }

    private int liczbaAktywnychGraczy() {
        return widokiAktywnychGraczy.size();
    }

    private int następnaPozycjaOd(int pozycja) {
        return (pozycja + 1) % liczbaAktywnychGraczy();
    }

    Widok nastepnyWidokOd(Widok widok) {
        int poz = indeksWidoku(widok);
        poz = (poz + 1) % liczbaAktywnychGraczy();
        return widokiAktywnychGraczy.get(poz);
    }

    boolean czyDynamitLeżyPrzed(Widok widok) {
        return przedKimLeżyDynamit() == widok;
    }

    void połóżDynamitPrzed(Widok widok) {
        dynamit.połóżPrzed(widok);
    }

    int jakDalekoDoSzeryfaOd(Widok widok) {
        if (widok.czyToSzeryf())
            return 0;
        int res = 1;
        Widok kolejny = nastepnyWidokOd(widok);
        while (!kolejny.czyToSzeryf()) {
            res++;
            kolejny = nastepnyWidokOd(kolejny);
        }
        return res;
    }

    Widok dajSzeryfaSpośród(List<Widok> lista) {
        for (Widok widok : lista) {
            if (widok.czyToSzeryf())
                return widok;
        }
        return null;
    }

    /* wspólna funkcja dla sił Prawa */
    List<Widok> podejrzaniOBandytyzm(List<Widok> lista) {
        List<Widok> l1 = dajSzeryfa().ktoDoMnieStrzelałSpośród(lista);
        List<Widok> l2 = zabiliWięcejPomocnikówNiżBandytów(lista);

        return Pomocnicza.suma(l1, l2);
    }

    List<Widok> dajBandytów(List<Widok> spośród, Widok ktoPyta) {
        List<Widok> res = new ArrayList<Widok>();

        for (Widok widok : spośród) {
            if (widok.pokażSwójRodzaj(ktoPyta) == Widok.RodzajWidoku.BANDYTA)
                res.add(widok);
        }

        return res;
    }

    List<Widok> mająNieWięcejŻyćNiż(List<Widok> lista, int liczbŻyć) {
        List<Widok> res = new ArrayList<>();
        for(Widok widok : lista) {
            if (widok.punktyŻycia() <= liczbŻyć)
                res.add(widok);
        }

        return res;
    }
}
