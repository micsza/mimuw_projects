package dzikizachod;

import java.util.Random;

public class WidokBandyty extends Widok {

    public WidokBandyty(Gracz gracz) {
        super((new Random()).nextInt(2) + 3, gracz);
        rodzajWidoku = RodzajWidoku.BANDYTA;
    }

    public RodzajWidoku pokażSwójRodzaj(Widok widokPytającego) {
        /* jesli pyta dzikizachod.Gra lub inny dzikizachod.Bandyta, odpowiedz prawde; wpp ukryj */
        if (stanWidoku() == StanWidoku.PRZESŁONIĘTY) {
            if (widokPytającego.rodzajWidoku() == RodzajWidoku.BANDYTA || widokPytającego.rodzajWidoku() == RodzajWidoku.GRA)
                return rodzajWidoku();
            return RodzajWidoku.UKRYTY;
        }

        return rodzajWidoku();
    }
}
