package dzikizachod;

import java.util.List;

public class StrategiaPomocnikaSzeryfaDomyslna extends StrategiaPomocnikaSzeryfa {

    public Decyzja jakaDecyzja(ObrazGry obrazGry, List<KartaAkcji> karty, Widok mójWidok) {
        assert(!karty.isEmpty());
        Akcja akcja = karty.get(0).akcja();

        if (akcja != Akcja.STRZEL)
            return jakaDecyzjaPomocnikaSzeryfa(obrazGry, akcja, mójWidok);

        /* STRZELA */
        List<Widok> wZasięgu = obrazGry.ktoWZasięgu(mójWidok);
        /* spr czy jest szeryf i go usun */
        Pomocnicza.usuń(wZasięgu, obrazGry.dajSzeryfa());
        Widok cel = Pomocnicza.wylosuj(wZasięgu);
        return new Decyzja(Decyzja.RodzajDecyzji.STRZEL, cel);
    }
}
