package dzikizachod;

import java.util.List;

public class StrategiaBandytyDomyslna extends StrategiaBandyty {
    public Decyzja jakaDecyzja(ObrazGry obrazGry, List<KartaAkcji> karty, Widok mójWidok) {
        assert(!karty.isEmpty());
        Akcja akcja = karty.get(0).akcja();
        if (akcja != Akcja.STRZEL)
            return jakaDecyzjaBandyty(obrazGry, akcja, mójWidok);

         /* strzela */
        return decyzjaDomyślnyStrzałBandyty(obrazGry, mójWidok);
    }
}
