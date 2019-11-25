package dzikizachod;

import java.util.List;

public class StrategiaBandytyCierpliwa extends StrategiaBandyty {

    public Decyzja jakaDecyzja(ObrazGry obrazGry, List<KartaAkcji> karty, Widok mójWidok) {
        assert(!karty.isEmpty());
        Akcja akcja = karty.get(0).akcja();
        if (akcja != Akcja.STRZEL)
            return jakaDecyzjaBandyty(obrazGry, akcja, mójWidok);

        /* STRZELA */
        List<Widok> wZasięgu = obrazGry.ktoWZasięgu(mójWidok);
        Widok cel = obrazGry.dajSzeryfaSpośród(wZasięgu);
        if (cel != null)
            return new Decyzja(Decyzja.RodzajDecyzji.STRZEL, cel);

        return decyzjaNicNieRób();
    }
}
