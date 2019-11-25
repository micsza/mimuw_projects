package dzikizachod;

import java.util.List;

public class StrategiaPomocnikaSzeryfaZliczajaca extends StrategiaPomocnikaSzeryfa {

    public Decyzja jakaDecyzja(ObrazGry obrazGry, List<KartaAkcji> karty, Widok mójWidok) {
        assert(!karty.isEmpty());
        Akcja akcja = karty.get(0).akcja();
        int mojaPozycja = obrazGry.indeksWidoku(mójWidok);
        assert(mojaPozycja != -1);

        if (akcja != Akcja.STRZEL)
            return jakaDecyzjaPomocnikaSzeryfa(obrazGry, akcja, mójWidok);

        List<Widok> wZasięgu = obrazGry.ktoWZasięgu(mójWidok);
        /* spr czy w zasięgu nie ma szeryfa */
        wZasięgu.remove(obrazGry.dajSzeryfa());

        List<Widok> podejrzani = obrazGry.podejrzaniOBandytyzm(wZasięgu);

        if (podejrzani.size() > 0) {
            Widok cel = Pomocnicza.wylosuj(podejrzani);
            return new Decyzja(Decyzja.RodzajDecyzji.STRZEL, cel);
        }

        return decyzjaNicNieRób();
    }


}
