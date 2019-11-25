package dzikizachod;

import java.util.Random;

public class WidokPomocnikaSzeryfa extends Widok {

    public WidokPomocnikaSzeryfa(Gracz gracz) {

        super((new Random()).nextInt(2) + 3, gracz);
        rodzajWidoku = RodzajWidoku.POMOCNIK_SZERYFA;
    }

    public RodzajWidoku pokażSwójRodzaj(Widok widokPytającego) {
        /* jeśli pyta dzikizachod.Gra odpowiedz prawdę, wpp nie zdradzaj sie */
        if (stanWidoku() == StanWidoku.PRZESŁONIĘTY) {
            if (widokPytającego.rodzajWidoku() == RodzajWidoku.GRA)
                return this.rodzajWidoku();
            return RodzajWidoku.UKRYTY;
        }

        return rodzajWidoku();
    }
}
