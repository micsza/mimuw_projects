/**
 * Created by tzetze on 09.06.17.
 */
public class Miasto {
    private String nazwa;
    private int koszt;

    public Miasto(String nazwa, int koszt) {
        this.nazwa = nazwa;
        this.koszt = koszt;
    }

    public String nazwa() {
        return nazwa;
    }

    public int koszt() {
        return koszt;
    }
}
