import java.util.ArrayList;

public class TurystaPragmatyczny extends Turysta {

    public TurystaPragmatyczny(String imię, double budżet, ArrayList<Wymaganie> wymagania) {
        super(imię, budżet, wymagania);
    }

    public boolean czyAkceptujePlan(Plan plan) {
        int ileTak = 0;
        int ileNie = 0;
        for (Wymaganie wymaganie : wymagania) {
            if (plan.czySpełniaWymaganie(wymaganie))
                ileTak++;
            else
                ileNie++;
        }

        return ileTak >= ileNie;
    }
}
