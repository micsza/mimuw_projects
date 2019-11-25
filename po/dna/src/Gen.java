import java.util.List;

/**
 * Created by tzetze on 15.06.17.
 */
public class Gen extends Podłańcuch {
    private String nazwaBiałka;

    public String nazwaBiałka() {
        return nazwaBiałka;
    }

    public Gen(List<ZasadyAzotowe> sekwencja, int indeksWGenomie, String nazwaBiałka) {
        super(sekwencja, indeksWGenomie);
        this.nazwaBiałka = nazwaBiałka;
    }

    public void transponuj(Genom a, Genom b) {

    }
}
