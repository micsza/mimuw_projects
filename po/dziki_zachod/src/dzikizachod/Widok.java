package dzikizachod;

import java.util.ArrayList;
import java.util.List;

/*
    dzikizachod.Widok jest awatarem/interfejsem gracza, który bierze udział w rozgrywce w jego imieniu. Zapewnia łatwiejszą modyfikację
    rozwiązania, gdyby zaszła potrzeba większego dbania o bezpieczeństwo.
 */
public abstract class Widok {
    public enum RodzajWidoku {
        GRA,
        SZERYF,
        POMOCNIK_SZERYFA,
        BANDYTA,
        UKRYTY
    }

    protected enum StanWidoku {
        PRZESŁONIĘTY,
        ODSŁONIĘTY
    }

    private static final int ZASIĘG_POCZĄTKOWY = 1;
    protected RodzajWidoku rodzajWidoku;
    private StanWidoku stanWidoku;
    private int punktyŻycia;
    private int poczatkowePunktyŻycia;
    private ArrayList<Widok> ktoDoMnieStrzelał = new ArrayList<>();
    private int zasięg;
    private Gracz gracz;

    /* specjalny kontruktor dla Widoku Gry */
    public Widok() {
        rodzajWidoku = RodzajWidoku.GRA;
    }

    public Widok(int punktyŻycia, Gracz gracz) {
        this.punktyŻycia = punktyŻycia;
        this.poczatkowePunktyŻycia = punktyŻycia;
        this.zasięg = ZASIĘG_POCZĄTKOWY;
        this.stanWidoku = StanWidoku.PRZESŁONIĘTY;
        this.gracz = gracz;
    }

    int początkowePunktyŻycia() {
        return poczatkowePunktyŻycia;
    }

    ArrayList<Widok> ktoDoMnieStrzelał() {
        return ktoDoMnieStrzelał;
    }

    int zasięg() {
        return zasięg;
    }

    public Gracz gracz() {
        return gracz;
    }

    RodzajWidoku pokażPrawdziwyRodzaj() {
        return rodzajWidoku;
    }

    int punktyŻycia() {
        return punktyŻycia;
    }

    StanWidoku stanWidoku() {
        return stanWidoku;
    }

    public void odkryj() {
        stanWidoku = StanWidoku.ODSŁONIĘTY;
    }

    public abstract RodzajWidoku pokażSwójRodzaj(Widok widokPytającego);

    protected RodzajWidoku rodzajWidoku() {
        return rodzajWidoku;
    }

    void odejmijPunktyŻycia(int ilePunktów) {
        punktyŻycia -= ilePunktów;
        if (punktyŻycia <= 0) {
            odkryjSię();
        }
    }

    boolean czyŻywy() {
        return punktyŻycia > 0;
    }

    void dodajPunktŻycia() {
        punktyŻycia += 1;
    }

    boolean czyZdrowy() {
        return punktyŻycia == poczatkowePunktyŻycia;
    }

    private void dodajPunktyŻycia(int ilePunktów) {
        punktyŻycia += ilePunktów;
    }

    boolean czyToSzeryf() {
        return rodzajWidoku == RodzajWidoku.SZERYF;
    }

    public void ulecz() {
        dodajPunktyŻycia(1);
    }

    private void odkryjSię() {
        stanWidoku = StanWidoku.ODSŁONIĘTY;
    }

    List<Widok> ktoDoMnieStrzelałSpośród(List<Widok> zbiór) {
        return Pomocnicza.przecięcie(zbiór, ktoDoMnieStrzelał());
    }

    void padłStrzał(Widok kto) {
        ktoDoMnieStrzelał.add(kto);
    }

    void zwiększZasięgO(int oIle) {
        zasięg += oIle;
    }

    public String toString() {
        return rodzajWidoku + "(" + ")";
    }
}
