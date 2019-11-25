import java.util.ArrayList;

public class Znudzony extends Wyborca {

    public Głos dajGłos(ArrayList<Kandydat> kandydaci) {
        Głos głos = new Głos(null);
        System.out.println(toString() + " mój głos: " + głos.toString());
        return głos;
    }
}
