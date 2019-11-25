import java.util.List;

/**
 * Created by tzetze on 15.06.17.
 */
public abstract class Podłańcuch {
    private List<ZasadyAzotowe> sekwencja;
    private int indeks;

    public Podłańcuch(List<ZasadyAzotowe> sekwencja, int indeks) {
        this.sekwencja = sekwencja; // deep copy?
        this.indeks = indeks;
    }

    public abstract void transponuj(Genom a, Genom b);
}
