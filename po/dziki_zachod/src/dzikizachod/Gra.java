package dzikizachod;

import java.util.*;

public class Gra {
    public enum StanGry {
        NIEUSTALONY,
        ROZGRYWKA_NIEMOŻLIWA,
        GRAMY,
        WYGRAŁ_SZERYF,
        WYGRALI_BANDYCI,
        REMIS
    }
    private static final int LICZBA_DOBIERANYCH_KART = 5;
    private static final boolean SUKCES = true;
    static final int SIŁA_RAŻENIA_DYNAMITU = 3;
    private final int LIMIT_TUR = 42;
    private static Widok widokGry = new WidokGry();

    private void ustawLosowoGraczy(ArrayList<Gracz> gracze) {
        Gracz szeryf = dajSzeryfa(gracze);
        assert(szeryf != null);
        gracze.remove(szeryf);
        Collections.shuffle(gracze);
        gracze.add(0, szeryf);
    }

    private Gracz dajSzeryfa(ArrayList<Gracz> gracze) {
        for (Gracz gracz : gracze)
            if (gracz.widok().czyToSzeryf())
                return gracz;
        return null;
    }

    private int ileKartBrakujeGraczowi(Gracz gracz) {
        if (gracz.ileKart() > LICZBA_DOBIERANYCH_KART)
            return -1;

        return LICZBA_DOBIERANYCH_KART - gracz.ileKart();
    }

    private boolean wydajKartyGraczowi(Gracz gracz, PulaAkcji pula) {
        while ((ileKartBrakujeGraczowi(gracz) > 0) && (!pula.czyPusta()))
            gracz.weźKartę(pula.dajKartę());
        if (ileKartBrakujeGraczowi(gracz) == 0)
            return SUKCES;
        return !SUKCES;
    }

    private void odbudujPulęKart(PulaAkcji pula, ArrayList<KartaAkcji> zużyte) {
        assert(pula.czyPusta());

        for (KartaAkcji karta : zużyte) {
            if (karta.akcja() != Akcja.DYNAMIT)
                pula.dodaj(karta);
        }
        pula.potasuj();
        zużyte.clear();
    }

    private boolean czyWystarczyKart(ArrayList<Gracz> gracze, PulaAkcji pula) {
        return pula.ileKartWPuli() >= LICZBA_DOBIERANYCH_KART; //gracze.size() * LICZBA_DOBIERANYCH_KART;
    }

    private void graczZabityPrzezDynamit(ObrazGry obrazGry, Gracz gracz) {
        obrazGry.graczZabityPrzez(gracz.widok(), null);
    }

    private ArrayList<Gracz> dajKopięGraczy(ArrayList<Gracz> graczeOryginalni) {
        ArrayList<Gracz> gracze = new ArrayList<>();
        for (Gracz gracz : graczeOryginalni)
            gracze.add(gracz.dajKopię());

        return gracze;
    }

    private void ustawWidoki(ArrayList<Gracz> gracze) {
        for (Gracz gracz : gracze)
            gracz.ustawWidok();
    }

    void rozgrywka(ArrayList<Gracz> graczeOryginalni, PulaAkcji pulaAkcji) {
        int numerTury = 1;
        StanGry stanGry = StanGry.NIEUSTALONY;
        boolean koniecGry = false;
        ArrayList<KartaAkcji> zużyteKarty = new ArrayList<>();
        System.out.println("** START");
        ArrayList<Gracz> gracze = dajKopięGraczy(graczeOryginalni);

        ustawWidoki(gracze);

        if (!czyWystarczyKart(gracze, pulaAkcji)) {
            System.out.println("[GRA] nie wystarczy kart");
            koniecGry = true;
            stanGry = StanGry.ROZGRYWKA_NIEMOŻLIWA;
            koniecGry = true;
            System.out.println("[GRA]" + stanGry);
        }
        else {
            pulaAkcji.potasuj();
            ustawLosowoGraczy(gracze);
            wydrukujGraczy(gracze, 2);
        }

        ObrazGry obrazGry = new ObrazGry(gracze);
        while (numerTury <= LIMIT_TUR && !koniecGry) {
            System.out.println("\n  ** TURA " + numerTury);

            for (int j = 0; j < gracze.size() && !koniecGry; j++) {
                Gracz gracz = gracze.get(j);

                // gracz się przedstawia
                System.out.print("\n  GRACZ " + (j + 1) + " " + gracz.rola() + ":");
                if (!gracz.czyŻywy()) {
                    System.out.println("\n" + wcięcie(4) + "MARTWY");
                    continue;
                }

                /* wydawanie kart graczowi */
                if (wydajKartyGraczowi(gracz, pulaAkcji) != SUKCES)
                    odbudujPulęKart(pulaAkcji, zużyteKarty);
                if (wydajKartyGraczowi(gracz, pulaAkcji) != SUKCES) {
                    System.err.println("Za mało kart akcji");
                    stanGry = StanGry.ROZGRYWKA_NIEMOŻLIWA;
                    koniecGry = true;
                }
                gracz.posortujKarty();
                System.out.print("\n" + wcięcie(4) + "Akcje: ");
                gracz.wydrukujKarty();

                /* najpierw dynamit */
                if (obrazGry.czyDynamitLeżyPrzed(gracz.widok())) {
                    System.out.print("\n" + wcięcie(6) +"dzikizachod.Dynamit: ");
                    if (gracz.rzućKostką() == 1) {
                        System.out.println("WYBUCHŁ");
                        obrazGry.wybuchłDynamitPrzed(gracz.widok());

                        if (!gracz.czyŻywy()) {
                            System.out.println(wcięcie(8) + "ZABIL MNIE DYNAMIT");
                            graczZabityPrzezDynamit(obrazGry, gracz);
                            break;
                        }
                    }
                    else {
                        System.out.println("PRZECHODZI DALEJ");
                        obrazGry.przestawDynamit();
                    }
                }
                /* wybuch dynamitu może zakończyć rozgrywkę */
                stanGry = obrazGry.sprawdźStanGry();
                if (stanGry != StanGry.GRAMY) {
                    koniecGry = true;
                    break;
                }

                /* iterujemy po kartach gracza */
                Iterator<KartaAkcji> iter = gracz.mojeKarty().iterator();
                System.out.println(wcięcie(4) + "Ruchy:");
                while(iter.hasNext()) {
                    KartaAkcji karta = iter.next();
                    Decyzja decyzjaGracza = gracz.podejmijDecyzję(obrazGry, karta);
                    if (decyzjaGracza.rodzajDecyzji() != Decyzja.RodzajDecyzji.NIC_NIE_RÓB) {
                        System.out.println(wcięcie(8) + decyzjaGracza.toString() + " " + indeksWidoku(gracze, decyzjaGracza.kogoDotyczy()));
                        wykonajAkcję(obrazGry, decyzjaGracza, gracz);
                        // spr czy szeryf lub bandyci zabici
                        stanGry= obrazGry.sprawdźStanGry();
                        if (stanGry != StanGry.GRAMY) {
                            koniecGry = true;
                            break;
                        }
                        zużyteKarty.add(karta);
                        iter.remove();
                    }
                } /* karty */

                System.out.println();
                wydrukujGraczy(gracze, 2);
            } /* gracze */

            if (stanGry != StanGry.GRAMY) {
                koniecGry = true;
                break;
            }

            numerTury++;
        } /* tury */

        if (stanGry == StanGry.GRAMY)
            stanGry = StanGry.REMIS;

        System.out.println("** KONIEC\n  WYGRANA STRONA: " + stanGry + ", # tur = " + numerTury);
    }

    private int indeksWidoku(ArrayList<Gracz> gracze, Widok widok) {
        assert(gracze.contains(widok.gracz()));
        return gracze.indexOf(widok.gracz()) + 1;
    }

    private void wykonajAkcję(ObrazGry obrazGry, Decyzja decyzjaGracza, Gracz gracz) {

        assert (decyzjaGracza.rodzajDecyzji() != Decyzja.RodzajDecyzji.NIC_NIE_RÓB);

        if (decyzjaGracza.rodzajDecyzji() == Decyzja.RodzajDecyzji.RZUĆ_DYNAMIT) {
            obrazGry.połóżDynamitPrzed(obrazGry.nastepnyWidokOd(gracz.widok()));
        }

        if (decyzjaGracza.rodzajDecyzji() == Decyzja.RodzajDecyzji.ULECZ) {
            decyzjaGracza.kogoDotyczy().dodajPunktŻycia();
        }

        if (decyzjaGracza.rodzajDecyzji() == Decyzja.RodzajDecyzji.ZWIĘKSZ_ZASIĘG_O_JEDEN) {
            decyzjaGracza.kogoDotyczy().zwiększZasięgO(1);
        }

        if (decyzjaGracza.rodzajDecyzji() == Decyzja.RodzajDecyzji.ZWIĘKSZ_ZASIĘG_O_DWA) {
            decyzjaGracza.kogoDotyczy().zwiększZasięgO(2);

        }

        if (decyzjaGracza.rodzajDecyzji() == Decyzja.RodzajDecyzji.STRZEL) {
            obrazGry.padłStrzał(gracz.widok(), decyzjaGracza.kogoDotyczy());
        }
    }

    private void wydrukujGraczy(ArrayList<Gracz> gracze, int n) {
        String s = wcięcie(n);
        System.out.println(s + "Gracze:");
        s += "  ";
        String g;
        for (Gracz gracz : gracze) {
            g = "" + (gracze.indexOf(gracz) + 1) + ": ";
            if (gracz.czyŻywy())
                g += gracz.toString();
            else
                g += "X";
            System.out.println(s + g);
        }
    }

    private void wydrukujGraczy(ArrayList<Gracz> gracze) {
        wydrukujGraczy(gracze, 0);
    }

    private static String wcięcie(int n) {
        if (n > 0) {
            char[] array = new char[n];
            Arrays.fill(array, ' ');
            return new String(array);
        }
        return "";
    }
}

