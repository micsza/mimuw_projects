package dzikizachod;

public class Bandyta extends Gracz {

    public Bandyta() {
        super(new StrategiaBandytyDomyslna());
    }

    Bandyta(Strategia strategia) {
        super(strategia);
    }

    public Bandyta dajKopię() {
        return new Bandyta(this.strategia());
    }

    public void ustawWidok() {
        this.widok = new WidokBandyty(this);
    }

    public String rola() {
        return "dzikizachod.Bandyta";
    }
}
