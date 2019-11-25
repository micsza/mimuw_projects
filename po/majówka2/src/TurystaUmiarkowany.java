import java.util.ArrayList;
import java.util.List;

public class TurystaUmiarkowany extends Turysta {

    public TurystaUmiarkowany(String imię, double budżet, List<Wymaganie> wymagania) {
        super(imię, budżet, wymagania);
    }

    public boolean czyAkceptujePlan(Plan plan) {
        for (Wymaganie wymaganie : wymagania) {
            if (plan.czySpełniaWymaganie(wymaganie))
                return true;
        }

        return false;
    }
}
