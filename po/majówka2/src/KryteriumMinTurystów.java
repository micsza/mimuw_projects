import java.util.ArrayList;

public class KryteriumMinTurystów extends Kryterium {

    int porównajPlany(Plan planA, Plan planB, ArrayList<Turysta> turyści) {
        int ileA = planA.iluStaćIZaakcpetuje(turyści);
        int ileB = planB.iluStaćIZaakcpetuje(turyści);

        return ileB - ileA;
    }
}
