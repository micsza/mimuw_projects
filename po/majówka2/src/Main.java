import java.util.ArrayList;
import java.util.List;

/**
 * Created by tzetze on 15.06.17.
 */
public class Main {

    public static void main(String[] args) {
        Miasto kraków = new Miasto("Kraków", 250);
        Miasto wrocław = new Miasto ("wrocław", 190);
        Miasto szczecin = new Miasto("Szczecin", 130);
        Miasto lublin = new Miasto("Lublin", 140);
        Miasto warszawa = new Miasto("Warszawa", 270);
        Miasto białystok = new Miasto("Białystok", 90);
        Miasto przemyśl = new Miasto("Przemyśl", 70);
        Miasto gdańsk = new Miasto("Gdańsk", 220);
        Miasto toruń = new Miasto("Toruń", 160);
        Miasto bydgoszcz = new Miasto("Bydgoszcz", 150);

        List<Miasto> miasta = new ArrayList<>();
        miasta.add(kraków);
        miasta.add(wrocław);
        miasta.add(szczecin);
        miasta.add(lublin);
        miasta.add(warszawa);
        miasta.add(białystok);
        miasta.add(przemyśl);
        miasta.add(gdańsk);
        miasta.add(toruń);
        miasta.add(bydgoszcz);

        Wymaganie chceKraków = new WymaganieChcęMiasto(kraków);
        Wymaganie chceWrocław = new WymaganieChcęMiasto(wrocław);
        Wymaganie chceSzczecin = new WymaganieChcęMiasto(szczecin);
        Wymaganie chceLublin = new WymaganieChcęMiasto(lublin);
        Wymaganie chceWarszawa = new WymaganieChcęMiasto(warszawa);
        Wymaganie chceBiałystok = new WymaganieChcęMiasto(białystok);

        Wymaganie nieChceKraków = new WymaganieNieChcęMiasta(kraków);
        Wymaganie nieChceToruń = new WymaganieNieChcęMiasta(toruń);
        Wymaganie nieChceBydgoszcz = new WymaganieNieChcęMiasta(bydgoszcz);
        Wymaganie nieChceWarszawa = new WymaganieNieChcęMiasta(warszawa);

        List<Wymaganie> wymaganie1 = new ArrayList<>();
        wymaganie1.add(chceBiałystok);
        wymaganie1.add(chceLublin);
        wymaganie1.add(chceWarszawa);
        wymaganie1.add(nieChceToruń);

        Turysta turysta1 = new TurystaUmiarkowany("Jasio", 350, wymaganie1);



    }
}
