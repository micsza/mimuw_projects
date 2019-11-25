package dzikizachod;

import java.util.List;

public class StrategiaBandytySprytna extends StrategiaBandyty {
    private enum StanStrategiiCierpliwej {
        SPRYTNA_NIEOKREŚLONA, /* w tej turze jeszcze nie zdecydowaliśmy się strzelać do swojego  */
        SPRYTNA_MĘCZENNIK, /* w tej turze już strzelałem do bandyty */
    }

    private StanStrategiiCierpliwej stan = StanStrategiiCierpliwej.SPRYTNA_NIEOKREŚLONA;
    private Widok męczennik; /* bandyta którego zdecydowałem się zabić w tej turze */

    public Decyzja jakaDecyzja(ObrazGry obrazGry, List<KartaAkcji> karty, Widok mójWidok) {
        assert(!karty.isEmpty());
        Akcja akcja = karty.get(0).akcja();

        if (akcja != Akcja.STRZEL)
            return jakaDecyzjaBandyty(obrazGry, akcja, mójWidok);

        /* dzikizachod.Strategia dla strzału */
        List<Widok> wZasięgu = obrazGry.ktoWZasięgu(mójWidok);
        /* Jeśli jest szeryf w zasięgu, to bez względu na to co dotychczas zrobiłeś strzelaj do niego. */
        Widok cel = obrazGry.dajSzeryfaSpośród(wZasięgu);
        if (cel != null) {
            sprawdźCzyKartaOstatnia(karty);
            return new Decyzja(Decyzja.RodzajDecyzji.STRZEL, cel);
        }

        /* Skoro nie ma szeryfa w zasięgu, to jeśli jeszcze nie próbowałeś strzelić do bandyty, to strzel jeśli możesz
        * zabić w jednej turze */
        if (stan == StanStrategiiCierpliwej.SPRYTNA_NIEOKREŚLONA) {
            int ileStrzałów = ileStrzałów(karty);
            List<Widok> bandyciDoOdstrzału = obrazGry.mająNieWięcejŻyćNiż(dajBandytówZ(wZasięgu, mójWidok), ileStrzałów);
            Pomocnicza.wydrukujListę(bandyciDoOdstrzału);
            if (bandyciDoOdstrzału.size() > 0) {
                stan = StanStrategiiCierpliwej.SPRYTNA_MĘCZENNIK;
                męczennik = Pomocnicza.wylosuj(bandyciDoOdstrzału);
                sprawdźCzyKartaOstatnia(karty);
                return new Decyzja(Decyzja.RodzajDecyzji.STRZEL, męczennik);
            }
            else {
                /* Nie ma bandyty do zabicia więc postępuj jak przy domyslnej */
                return decyzjaDomyślnyStrzałBandyty(obrazGry, mójWidok);
            }
        }

        if (stan == StanStrategiiCierpliwej.SPRYTNA_MĘCZENNIK) {
            if (męczennik.czyŻywy()) {
                sprawdźCzyKartaOstatnia(karty);
                return new Decyzja(Decyzja.RodzajDecyzji.STRZEL, męczennik);
            }
            else {
                sprawdźCzyKartaOstatnia(karty);
                return decyzjaDomyślnyStrzałBandyty(obrazGry, mójWidok);
            }
        }

        return decyzjaNicNieRób();
    }

    private void sprawdźCzyKartaOstatnia(List<KartaAkcji> karty) {
        if (karty.size() == 1) {
            stan = StanStrategiiCierpliwej.SPRYTNA_NIEOKREŚLONA;
        }
    }
}
