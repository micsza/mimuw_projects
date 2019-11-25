package dzikizachod;

import java.util.ArrayList;
import java.util.List;

public abstract class Strategia {

    /* zawsze kartą do zagrania jest pierwsza z kart */
    protected abstract Decyzja jakaDecyzja(ObrazGry obrazGry, List<KartaAkcji> karty, Widok mójWidok);

    /* domyślne decyzje dla wszystkich rodzajów postaci */

    protected Decyzja decyzjaUlecz(Widok widok) {
        if (widok.czyZdrowy())
            return new Decyzja(Decyzja.RodzajDecyzji.NIC_NIE_RÓB, widok);
        return new Decyzja(Decyzja.RodzajDecyzji.ULECZ, widok);
    }

    protected Decyzja decyzjaZasięgPlusJeden(Widok komu) {
        return new Decyzja((Decyzja.RodzajDecyzji.ZWIĘKSZ_ZASIĘG_O_JEDEN), komu);
    }

    protected Decyzja decyzjaZasięgPlusDwa(Widok komu) {
        return new Decyzja((Decyzja.RodzajDecyzji.ZWIĘKSZ_ZASIĘG_O_DWA), komu);
    }

    protected Decyzja decyzjaRzućDynamit(Widok ktoRzuca) {
        return new Decyzja(Decyzja.RodzajDecyzji.RZUĆ_DYNAMIT, ktoRzuca);
    }

    protected static Decyzja decyzjaNicNieRób() {
        return new Decyzja(Decyzja.RodzajDecyzji.NIC_NIE_RÓB, null);
    }

    protected static int ileStrzałów(List<KartaAkcji> karty) {
        int res = 0;
        for (KartaAkcji karta : karty) {
            if (karta.akcja() == Akcja.STRZEL)
                res++;
        }
        return res;
    }

    private static ArrayList<Widok> dajRodzajZ(List<Widok> lista, Widok.RodzajWidoku rodzajWidoku, Widok mójWidok) {
        ArrayList<Widok> res = new ArrayList<>();
        for (Widok widok : lista) {
            if (widok.pokażSwójRodzaj(mójWidok) == rodzajWidoku)
                res.add(widok);
        }
        return res;
    }

    protected static ArrayList<Widok> dajBandytówZ(List<Widok> lista, Widok mójWidok) {
        return dajRodzajZ(lista, Widok.RodzajWidoku.BANDYTA, mójWidok);
    }

    protected static ArrayList<Widok> dajPomocnikówZ(List<Widok> lista, Widok mójWidok) {
        return dajRodzajZ(lista, Widok.RodzajWidoku.UKRYTY, mójWidok);
    }
}
