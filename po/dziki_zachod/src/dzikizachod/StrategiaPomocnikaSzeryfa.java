package dzikizachod;

import java.util.List;

public abstract class StrategiaPomocnikaSzeryfa extends Strategia {

    protected Decyzja jakaDecyzjaPomocnikaSzeryfa(ObrazGry obrazGry, Akcja akcja, Widok mójWidok)  {


        if (akcja == Akcja.ULECZ) {
            Widok szeryfWZasięgu = obrazGry.czySzeryfWZasięgu(mójWidok);
            if ((szeryfWZasięgu != null) && (!szeryfWZasięgu.czyZdrowy()))
                return decyzjaUlecz(szeryfWZasięgu);
            else {
                if (!mójWidok.czyZdrowy())
                    return decyzjaUlecz(mójWidok);
                else
                    return decyzjaNicNieRób();
            }
        }

        if (akcja == Akcja.ZASIEG_PLUS_JEDEN) {
            return decyzjaZasięgPlusJeden(mójWidok);
        }

        if (akcja == Akcja.ZASIEG_PLUS_DWA) {
            return decyzjaZasięgPlusDwa(mójWidok);
        }

        /* rzut dynamitem */
        int odległośćDoSzeryfa = obrazGry.jakDalekoDoSzeryfaOd(mójWidok);
        if (odległośćDoSzeryfa > 3) {
            List<Widok> graczeNaPrzódDoSzeryfa = obrazGry.ktoWZasięguDoPrzodu(mójWidok);
            List<Widok> podejrzani = obrazGry.podejrzaniOBandytyzm(graczeNaPrzódDoSzeryfa);
            if (3 * podejrzani.size() >= 2 * graczeNaPrzódDoSzeryfa.size() )
                return decyzjaRzućDynamit(mójWidok);
        }

        return decyzjaNicNieRób();
    }
}
