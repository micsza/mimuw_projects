import java.util.ArrayList;

public class Feminista extends Wyborca {

    public Głos dajGłos(ArrayList<Kandydat> kandydaci) {
        Głos głos;
        ArrayList<Kandydat> kobiety = Pomocnicza.dajKandydatówOPłci(kandydaci, Płeć.KOBIETA);

        if (kobiety.isEmpty())
            głos = new Głos(null);
        else {
            Kandydat kto = kobiety.get(0);
            for (int j = 1; j < kobiety.size(); j++) {
                if (kobiety.get(j).nazwisko().compareTo(kto.nazwisko()) < 0)
                    kto = kobiety.get(j);
            }
            głos = new Głos(kto);
        }

        System.out.println(toString() + " mój głos: " + głos.toString());
        return głos;
    }
}
