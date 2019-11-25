public class Farba {
    private Kolor kolor;
    private double jakość;
    private double toksyczność;
    private static final String etykieta = "[FARBA] ";

    public Farba(Kolor kolor, double jakość, double toksyczność) {
        this.kolor = kolor;
        this.jakość = jakość;
        this.toksyczność = toksyczność;
    }

    Kolor kolor() {
        return kolor;
    }

    double jakość() {
        return jakość;
    }

    double toksyczność() {
        return toksyczność;
    }

    void mieszajSięWg(Reguła reguła) {
        assert(reguła.kolorWejściowy() != this.kolor()); // todo obsłuzyc to błędem?
        System.out.print(etykieta + this.toString() + " mieszam się wg reguły " + reguła.toString() + " ... ");

        Pigment pigment = reguła.pigment();
        kolor = reguła.kolorWyjściowy();

        if (pigment.rodzajModyfikatoraJakości() == RodzajModyfikatora.PLUS)
            jakość += pigment.modyfikatorJakości();
        else
            jakość *= pigment.modyfikatorJakości();
        if (jakość < 0)
            jakość = 0;
        else
            if (jakość > 100)
                jakość = 100;

        if (pigment.rodzajModyfikatoraToksyczności() == RodzajModyfikatora.PLUS)
            toksyczność += pigment.modyfikatorToksyczności();
        else
            toksyczność *= pigment.modyfikatorToksyczności();
        if (toksyczność < 0)
            toksyczność = 0;
        else
            if (toksyczność > 100)
                toksyczność = 100;
        System.out.println("powstałam nowa: " + this.toString());
    }

    public Farba dajPróbkę() {
        return new Farba(this.kolor, this.jakość, this.toksyczność);
    }

    public String toString() {
        return "<" + kolor.toString() + ", " + toksyczność + ", " + jakość + ">";
    }
}
