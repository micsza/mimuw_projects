package dzikizachod;

public abstract class StrategiaSzeryfa extends Strategia {

    protected Decyzja jakaDecyzjaSzeryfa(ObrazGry obrazGry, Akcja akcja, Widok mójWidok)  {
        int mojaPozycja = obrazGry.widokiAktywnychGraczy().indexOf(mójWidok);

        if (akcja == Akcja.ULECZ) {
            return decyzjaUlecz(mójWidok);
        }

        if (akcja == Akcja.ZASIEG_PLUS_JEDEN) {
            return decyzjaZasięgPlusJeden(mójWidok);
        }

        if (akcja == Akcja.ZASIEG_PLUS_DWA) {
            return decyzjaZasięgPlusDwa(mójWidok);
        }

        return decyzjaRzućDynamit(mójWidok);
    }
}
