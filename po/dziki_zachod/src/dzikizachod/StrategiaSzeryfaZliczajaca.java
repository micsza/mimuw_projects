package dzikizachod;

import java.util.List;

public class StrategiaSzeryfaZliczajaca extends StrategiaSzeryfa {

    public Decyzja jakaDecyzja(ObrazGry obrazGry, List<KartaAkcji> karty, Widok mójWidok) {
        assert(!karty.isEmpty());
        Akcja akcja = karty.get(0).akcja();

        int mojaPozycja = obrazGry.indeksWidoku(mójWidok);
        assert(mojaPozycja != -1);

        if (akcja != Akcja.STRZEL)
            return jakaDecyzjaSzeryfa(obrazGry, akcja, mójWidok);

        List<Widok> wZasięgu = obrazGry.ktoWZasięgu(mójWidok);
        List<Widok> podejrzani = obrazGry.podejrzaniOBandytyzm(wZasięgu);

        //System.out.print("Podejrzani o bandytyzm: ");
        //dzikizachod.Pomocnicza.wydrukujListę(podejrzani);

        if (podejrzani.size() > 0) {
            Widok cel = Pomocnicza.wylosuj(podejrzani);
            return new Decyzja(Decyzja.RodzajDecyzji.STRZEL, cel);
        }

        return decyzjaNicNieRób();
    }
}
