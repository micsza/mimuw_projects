import java.util.ArrayList;
import java.util.List;

/**
 * Created by tzetze on 09.06.17.
 */
public abstract class Turysta {
    private String imię;
    private double budżet;
    protected List<Wymaganie> wymagania;

    public Turysta(String imię, double budżet, List<Wymaganie> wymagania) {
        this.imię = imię;
        this.budżet = budżet;
        this.wymagania = wymagania;
    }

    public String imię() {
        return imię;
    }

    public double budżet() {
        return budżet;
    }

    public List<Wymaganie> wymagania() {
        return wymagania;
    }

    public boolean czyStaćNaPlan(Plan plan) {
        return budżet >= plan.koszt();

    }

    public abstract boolean czyAkceptujePlan(Plan plan);
}
