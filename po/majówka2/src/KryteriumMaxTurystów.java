import java.util.ArrayList;

// todo Singleton!
public class KryteriumMaxTurystów extends Kryterium {

    int porównajPlany(Plan planA, Plan planB, ArrayList<Turysta> turyści) {
        int ileA = planA.iluStaćIZaakcpetuje(turyści);
        int ileB = planB.iluStaćIZaakcpetuje(turyści);

        return ileA - ileB;
    }
}
