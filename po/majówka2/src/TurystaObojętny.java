import java.util.ArrayList;

/**
 * Created by tzetze on 09.06.17.
 */
public class TurystaObojętny extends Turysta {

    public TurystaObojętny(String imię, double budżet, ArrayList<Wymaganie> wymagania) {
        super(imię, budżet, wymagania);
    }

    public boolean czyAkceptujePlan(Plan plan) {
        return true;
    }
}
