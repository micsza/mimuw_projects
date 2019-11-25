import java.util.ArrayList;
import java.util.List;

/**
 * Created by tzetze on 15.06.17.
 */
public class ZmianaDodaj extends Zmiana {
    private Miasto miastoDoDodania;


    public ZmianaDodaj(Miasto miasto) {
        miastoDoDodania = miasto;
    }

    public Plan zmieńPlan(Plan plan) {
        return dodajMiastoDoPlanu(plan, miastoDoDodania);
    }
}
