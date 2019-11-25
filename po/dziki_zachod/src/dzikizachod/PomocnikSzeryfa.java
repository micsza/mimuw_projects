package dzikizachod;

public class PomocnikSzeryfa extends Gracz {
    public PomocnikSzeryfa() {
        super(new StrategiaPomocnikaSzeryfaDomyslna());
    }

    public PomocnikSzeryfa(Strategia strategia) {
        super(strategia);
    }

    public PomocnikSzeryfa dajKopię() {
        return new PomocnikSzeryfa( this.strategia());
    }

    public void ustawWidok() {
        this.widok = new WidokPomocnikaSzeryfa(this);
    }

    public String rola() {
        return "Pomocnik Szeryfa";
    }
}
