package dzikizachod;

public class WidokSzeryfa extends Widok {

    private static final int POCZĄTKOWE_PUNKTY_ŻYCIA_SZERYFA = 5;

    public WidokSzeryfa(Gracz gracz) {

        super(POCZĄTKOWE_PUNKTY_ŻYCIA_SZERYFA, gracz);
        rodzajWidoku = RodzajWidoku.SZERYF;
    }
    public RodzajWidoku pokażSwójRodzaj(Widok widokPytającego) {
        /* bez wgledu na to kto pyta, odpowiedz prawdę */
        return rodzajWidoku();
    }


}
