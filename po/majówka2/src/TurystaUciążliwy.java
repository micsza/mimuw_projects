import java.util.ArrayList;

/**
 * Created by tzetze on 15.06.17.
 */
public class TurystaUciążliwy extends Turysta {
    public TurystaUciążliwy(String imię, double budżet, ArrayList<Wymaganie> wymagania) {
        super(imię, budżet, wymagania);
    }

    public boolean czyAkceptujePlan(Plan plan) {
        for (Wymaganie wymaganie : wymagania) {
            if (!plan.czySpełniaWymaganie(wymaganie))
                return false;
        }

        return true;
    }


}
