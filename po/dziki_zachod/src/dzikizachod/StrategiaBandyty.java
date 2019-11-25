package dzikizachod;

import java.util.ArrayList;
import java.util.List;

public abstract class StrategiaBandyty extends Strategia {

    protected Decyzja jakaDecyzjaBandyty(ObrazGry obrazGry, Akcja akcja, Widok mójWidok) {

        if (akcja == Akcja.ULECZ) {
            if (!mójWidok.czyZdrowy())
                return decyzjaUlecz(mójWidok);
            else
                return decyzjaNicNieRób();
        }

        if (akcja == Akcja.ZASIEG_PLUS_JEDEN) {
            return decyzjaZasięgPlusJeden(mójWidok);
        }

        if (akcja == Akcja.ZASIEG_PLUS_DWA) {
            return decyzjaZasięgPlusDwa(mójWidok);
        }

        /* rzut dynamitem */
        int odległośćDoSzeryfa = obrazGry.jakDalekoDoSzeryfaOd(mójWidok);
        if (odległośćDoSzeryfa < 3) {
            return decyzjaRzućDynamit(mójWidok);
        }

        return decyzjaNicNieRób();
    }

    protected static Decyzja decyzjaDomyślnyStrzałBandyty(ObrazGry obrazGry, Widok mójWidok) {
        /* Domyślny Strzał do szeryfa lub pomocnika */
        List<Widok> wZasięgu = obrazGry.ktoWZasięgu(mójWidok);
        Widok cel = obrazGry.dajSzeryfaSpośród(wZasięgu);
        if (cel != null)
            return new Decyzja(Decyzja.RodzajDecyzji.STRZEL, cel);
        else {
            ArrayList<Widok> pomocnicy = dajPomocnikówZ(wZasięgu, mójWidok);
            if (pomocnicy.size() > 0) {
                cel = Pomocnicza.wylosuj(pomocnicy);
                return new Decyzja(Decyzja.RodzajDecyzji.STRZEL, cel);
            }
        }
        return decyzjaNicNieRób();
    }
}
