import java.util.ArrayList;

public class Wybory {
    private ArrayList<Kandydat> kandydaci;

    public Wybory(ArrayList<Kandydat> kandydaci) {
        this.kandydaci = kandydaci;
        System.out.println("*** Kandydaci:");
        for (Kandydat kandydat : kandydaci) {
            System.out.println(kandydat.toString());
        }
        System.out.println("----------");
    }

    public WynikWyborów przeprowadźWybory(ArrayList<Wyborca> wyborcy) {
        WynikWyborów wynik = new WynikWyborów(kandydaci);


        for (Wyborca wyborca : wyborcy) {
            wynik.dodajGłos(wyborca.dajGłos(kandydaci));
        }

        return wynik;
    }


}
