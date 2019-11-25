package dzikizachod;

import java.util.List;

public class StrategiaSzeryfaDomyslna extends StrategiaSzeryfa {

    public Decyzja jakaDecyzja(ObrazGry obrazGry, List<KartaAkcji> karty, Widok mójWidok) {
        assert(!karty.isEmpty());
        Akcja akcja = karty.get(0).akcja();
        int mojaPozycja = obrazGry.indeksWidoku(mójWidok);
        assert(mojaPozycja != -1);

        if (akcja != Akcja.STRZEL)
            return jakaDecyzjaSzeryfa(obrazGry, akcja, mójWidok);

        /* STRZELA */
        List<Widok> wZasięgu = obrazGry.ktoWZasięgu(mójWidok);
        List<Widok> wZasięguIStrzelał = mójWidok.ktoDoMnieStrzelałSpośród(wZasięgu);
        Widok cel;
        if (!wZasięguIStrzelał.isEmpty()) {
            cel = Pomocnicza.wylosuj(wZasięguIStrzelał);
        }
        else
            cel = Pomocnicza.wylosuj(wZasięgu);

        return new Decyzja(Decyzja.RodzajDecyzji.STRZEL, cel);
    }
}
