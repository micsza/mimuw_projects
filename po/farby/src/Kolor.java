import java.util.ArrayList;
import java.util.HashMap;

public class Kolor {
    private String nazwa;
    //private static ArrayList<Kolor> paletaKolorów = new ArrayList<>();
    private static HashMap<String, Kolor> paletaKolorów = new HashMap<>();

    private Kolor(String nazwa) {
        this.nazwa = nazwa;
    }

    public static Kolor dajKolor(String nazwaKoloru) {
        if (paletaKolorów.containsKey(nazwaKoloru))
            return paletaKolorów.get(nazwaKoloru);

        Kolor kolor = new Kolor(nazwaKoloru);
        paletaKolorów.put(nazwaKoloru, kolor);
        return kolor;

    }

    public String nazwa() {
        return nazwa;
    }

    public String toString() {
        return nazwa;
    }

    public static int rozmiarPalety() {
        return paletaKolorów.size();
    }

}
