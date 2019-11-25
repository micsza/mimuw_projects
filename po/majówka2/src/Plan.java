import java.util.ArrayList;
import java.util.List;

public class Plan {
    private List<Miasto> listaMiastDoOdwiedzenia = new ArrayList<>();

    public void dodajDoPlanu(Miasto miasto) {
        listaMiastDoOdwiedzenia.add(miasto);
    }

    public Plan(List<Miasto> listaMiast) {
        listaMiastDoOdwiedzenia = listaMiast;
    }

    public List<Miasto> listaMiastDoOdwiedzenia() {
        return listaMiastDoOdwiedzenia;
    }

    public double koszt() {
        int res = 0;
        for (Miasto miasto : listaMiastDoOdwiedzenia)
            res += miasto.koszt();
        return res;
    }

    public int iluStaćIZaakcpetuje(ArrayList<Turysta> turyści) {
        int res = 0;

        for (Turysta turysta : turyści) {
            if (turysta.czyStaćNaPlan(this) && turysta.czyAkceptujePlan(this))
                res++;
        }

        return res;
    }

    public boolean czySpełniaWymaganie(Wymaganie wymaganie) {
        return wymaganie.czyPlanSpełnia(this);
    }

    public boolean czyZawieraMiasto(Miasto miasto) {
        return listaMiastDoOdwiedzenia.contains(miasto);
    }
}
