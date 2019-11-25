import java.io.IOException;
import java.util.ArrayList;

public class Tester {

    public static void main(String[] args) {
        Kolor kolor = Kolor.dajKolor("różowy");
        System.out.println(kolor + " rozmiar palety = " + Kolor.rozmiarPalety());

        Kolor kolor2 = Kolor.dajKolor("niebieski");
        System.out.println(kolor2 + " rozmiar palety = " + Kolor.rozmiarPalety());

        Kolor kolor3 = Kolor.dajKolor("niebieski");
        System.out.println(kolor3 + " rozmiar palety = " + Kolor.rozmiarPalety());

        System.out.println("dir = " + System.getProperty("user.dir"));

        Parser parser = new Parser();
        try {
            parser.wczytajScieżkiPlików();
        } catch (IOException e) {
            System.err.println("Błąd odczytu ścieżek plików");
        }

        try {
            parser.wczytajŚcieżkiPlików2();
        } catch (IOException e) {
            System.err.println("Błąd odczytu ścieżek plików 2");
        }

        try {
            parser.wczytajFarby();
        } catch (IOException e) {
            System.err.println("Błąd odczytu farb");
        }

        ArrayList<Reguła> reguły = new ArrayList<>();
        try {
            parser.wczytajPigmentyIReguły(reguły);
        } catch (IOException e) {
            System.err.println("Błąd odczytu pigmentów");
        }

        System.out.println("reguły = " + reguły);

        System.out.println("---------------------------------------");

        EmulatorMaszyny maszyna = new EmulatorMaszyny();
        maszyna.ustawKonfigurację();
        maszyna.ustawFarbęWMieszalniku();
        maszyna.mieszajFarbę(maszyna.pigmenty().get(0));
        System.out.println("farba w mieszalniku: " + maszyna.farbaWMieszalniku());



    }
}
