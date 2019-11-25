import java.util.ArrayList;
import java.util.Random;

public class Partyjniak extends Wyborca {
    private Partia mojaPartia;

    public Partyjniak(Partia mojaPartia) {
        this.mojaPartia = mojaPartia;
    }

    public Partia mojaPartia() {
        return mojaPartia;
    }

    public Głos dajGłos(ArrayList<Kandydat> kandydaci) {
        ArrayList<Kandydat> swoi = Pomocnicza.dajKandydatówPartii(kandydaci, mojaPartia);
        if (swoi.isEmpty())
            return null;
        Random rand = new Random();
        Głos głos = new Głos(swoi.get(rand.nextInt(swoi.size())));
        System.out.println(toString() + " mój głos: " + głos.toString());
        return głos;
    }

    public String toString() {
        return "Partyjniak(" + mojaPartia.toString() + ")";
    }
}
