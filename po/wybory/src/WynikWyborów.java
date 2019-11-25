import java.util.*;
import java.util.stream.Collectors;

public class WynikWyborów {
    private HashMap<Kandydat, Integer> wynik = new HashMap<>();
    private int ilePustych;
    private int ileGłosów;

    public WynikWyborów(ArrayList<Kandydat> kandydaci) {
        for (Kandydat kandydat : kandydaci) {
            wynik.put(kandydat, 0);
        }
        ilePustych = 0;
    }

    public void dodajGłos(Głos głos) {
        ileGłosów++;
        if (głos.naKogo() == null)
            ilePustych++;
        else {
            wynik.put(głos.naKogo(), wynik.getOrDefault(głos.naKogo(), 0) + 1);
        }
    }

    public String pokażWynik() {
        Map<Kandydat, Integer> wynikPosortowany = Pomocnicza.<Kandydat, Integer>posortujMapęPoWartościach(wynik);
        int ileWażnych = ileGłosów - ilePustych;
        double turnout = (double) (ileWażnych) / ileGłosów * 100;
        String res = "\n*** Wyniki: ";
        res += "# wszystkich głosów = " + (ileGłosów) + ", # pustych = " + ilePustych + "\nTurnout = "
                + String.format("%1$.2f %%\n", turnout);
        for (HashMap.Entry<Kandydat, Integer> entry : wynikPosortowany.entrySet()) {
            res += entry.getKey() + ": " + String.format("%1.2f %%\n", (double) entry.getValue() / ileWażnych * 100);
        }

        return res;
    }
}
