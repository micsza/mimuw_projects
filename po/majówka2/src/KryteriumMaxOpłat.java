import java.util.ArrayList;

public class KryteriumMaxOpłat extends Kryterium {

    int porównajPlany(Plan planA, Plan planB, ArrayList<Turysta> turyści) {
        int ileA = planA.iluStaćIZaakcpetuje(turyści);
        int ileB = planB.iluStaćIZaakcpetuje(turyści);

        return (int) (ileA * planA.koszt() - ileB * planB.koszt());
    }

}
