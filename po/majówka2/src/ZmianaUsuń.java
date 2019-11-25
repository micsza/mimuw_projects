import java.util.ArrayList;
import java.util.List;

/**
 * Created by tzetze on 15.06.17.
 */
public class ZmianaUsuń extends Zmiana {
    private Miasto miastoDoUsunięcia;

    public ZmianaUsuń(Miasto miasto) {
        miastoDoUsunięcia = miasto;
    }

    public Plan zmieńPlan(Plan plan) {
        return usuńMiastoZPlanu(plan, miastoDoUsunięcia);
    }
}
