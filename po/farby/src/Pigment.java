import java.util.HashMap;

public class Pigment {
    private String nazwa;
    private RodzajModyfikatora rodzajModyfikatoraJakości;
    private RodzajModyfikatora rodzajModyfikatoraToksyczności;
    private double modyfikatorJakości;
    private double modyfikatorToksyczności;

    private static HashMap<String, Pigment> paletaPigmentów = new HashMap<>();

    private Pigment(String nazwa, RodzajModyfikatora rodzajModyfikatoraJakości, double modyfikatorJakości,
                   RodzajModyfikatora rodzajModyfikatoraToksyczności, double modyfikatorToksyczności) {
        this.nazwa = nazwa;
        this.rodzajModyfikatoraJakości = rodzajModyfikatoraJakości;
        this.modyfikatorJakości = modyfikatorJakości;
        this.rodzajModyfikatoraToksyczności = rodzajModyfikatoraToksyczności;
        this.modyfikatorToksyczności = modyfikatorToksyczności;
    }

    public static Pigment dajPigment(String nazwaPigmentu, RodzajModyfikatora rodzajModyfikatoraJakości,
                                     double modyfikatorJakości, RodzajModyfikatora rodzajModyfikatoraToksyczności,
                                     double modyfikatorToksyczności) {
        if (paletaPigmentów.containsKey(nazwaPigmentu))
            return paletaPigmentów.get(nazwaPigmentu);

        Pigment pigment = new Pigment(nazwaPigmentu, rodzajModyfikatoraJakości, modyfikatorJakości,
                rodzajModyfikatoraToksyczności, modyfikatorToksyczności);
        paletaPigmentów.put(nazwaPigmentu, pigment);
        return pigment;
    }

    public String nazwa() {
        return nazwa;
    }

    public RodzajModyfikatora rodzajModyfikatoraJakości() {
        return rodzajModyfikatoraJakości;
    }

    public RodzajModyfikatora rodzajModyfikatoraToksyczności() {
        return rodzajModyfikatoraToksyczności;
    }

    public double modyfikatorJakości() {
        return modyfikatorJakości;
    }


    public double modyfikatorToksyczności() {
        return modyfikatorToksyczności;
    }

    boolean equals(Pigment pigment) {
        return nazwa.equals(pigment.nazwa());
    }

    public String toString() {
        return "<" + nazwa + ", " + rodzajModyfikatoraToksyczności + " " + modyfikatorToksyczności + ", " +
                rodzajModyfikatoraJakości + " " + modyfikatorJakości + ">";
    }
}
