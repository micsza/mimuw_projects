import java.util.ArrayList;
import java.util.List;

public abstract class Zmiana {

    public abstract Plan zmieńPlan(Plan plan);

    public Plan dodajMiastoDoPlanu(Plan plan, Miasto miastoDoDodania) {
        List<Miasto> res = new ArrayList<>();
        for (Miasto miasto : plan.listaMiastDoOdwiedzenia())
            res.add(miasto);

        res.add(miastoDoDodania);

        return new Plan(res);
    }

    public Plan usuńMiastoZPlanu(Plan plan, Miasto miastoDoUsunięcia) {
        if (!plan.czyZawieraMiasto(miastoDoUsunięcia)) {
            return null;
        }

        List<Miasto> res = new ArrayList<>();

        for (Miasto miasto : plan.listaMiastDoOdwiedzenia()) {
            if (!miasto.equals(miastoDoUsunięcia))
                res.add(miasto);
        }

        return new Plan(res);
    }
}
