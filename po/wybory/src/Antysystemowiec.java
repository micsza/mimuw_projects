import java.util.ArrayList;
import java.util.Random;

public class Antysystemowiec extends Wyborca {

    public Głos dajGłos(ArrayList<Kandydat> kandydaci) {
        Głos głos;
        if (Math.random() < 0.5)
            głos = new Głos(null);
        else {
            ArrayList<Kandydat> niezależni = Pomocnicza.dajKandydatówPartii(kandydaci, null);
            Random rand = new Random();
            if (niezależni.isEmpty())
                głos = new Głos(null);
            else
                głos = new Głos(niezależni.get(rand.nextInt(niezależni.size())));
        }
        System.out.println(toString() + " mój głos: " + głos.toString());

        return głos;
    }
}
