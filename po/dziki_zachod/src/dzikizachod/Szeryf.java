package dzikizachod;

public class Szeryf extends Gracz {
    public Szeryf() {
        super(new StrategiaSzeryfaDomyslna());
    }

    public Szeryf(Strategia strategia) {
        super(strategia);
    }

    public Szeryf dajKopię() {
        return new Szeryf(this.strategia());
    }

    public void ustawWidok() {
        this.widok = new WidokSzeryfa(this);
    }

    public String rola() {
        return "dzikizachod.Szeryf";
    }
}
