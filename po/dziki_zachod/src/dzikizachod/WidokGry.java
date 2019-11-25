package dzikizachod;

/* specjalna klasa Widoku dla obiektu dzikizachod.Gra */
public class WidokGry extends Widok {
    public WidokGry() {

        super();
    }

    public RodzajWidoku pokażSwójRodzaj(Widok widokPytającego) {
        /* nikt o to nie zapyta ale trzeba zaimplementowac */
        return rodzajWidoku();
    }
}
