package dzikizachod;

import java.util.ArrayList;

public class Tester {

    public static void main(String[] argv) {

        /* pula kart */
        PulaAkcji pulaAkcji = new PulaAkcji();
        pulaAkcji.dodaj(Akcja.STRZEL, 20);
        pulaAkcji.dodaj(Akcja.ULECZ, 18);
        pulaAkcji.dodaj(Akcja.ZASIEG_PLUS_DWA, 3);
        pulaAkcji.dodaj(Akcja.DYNAMIT, 1);
        pulaAkcji.dodaj(Akcja.ZASIEG_PLUS_JEDEN, 7);

        /* gracze */
        ArrayList<Gracz> gracze = new ArrayList<>();
        Szeryf szeryf = new Szeryf(new StrategiaSzeryfaZliczajaca());
        gracze.add(szeryf);
        gracze.add(new Bandyta(new StrategiaBandytyCierpliwa()));
        gracze.add(new Bandyta(new StrategiaBandytySprytna()));
        //gracze.add(new dzikizachod.Bandyta("Baltazar", new dzikizachod.StrategiaBandytySprytna()));
        gracze.add(new Bandyta());
        gracze.add(new Bandyta());
        gracze.add(new PomocnikSzeryfa(new StrategiaPomocnikaSzeryfaZliczajaca()));
        gracze.add(new PomocnikSzeryfa(new StrategiaPomocnikaSzeryfaZliczajaca()));
        gracze.add(new PomocnikSzeryfa(new StrategiaPomocnikaSzeryfaZliczajaca()));
        gracze.add(new PomocnikSzeryfa(new StrategiaPomocnikaSzeryfaZliczajaca()));
        gracze.add(new PomocnikSzeryfa());
        gracze.add(new PomocnikSzeryfa());
        gracze.add(new PomocnikSzeryfa());

        /* gra */
        Gra gra = new Gra();
        gra.rozgrywka(gracze, pulaAkcji);

    }
}
