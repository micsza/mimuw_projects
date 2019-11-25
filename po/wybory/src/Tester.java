import java.util.ArrayList;

public class Tester {

    public static void main(String[] args) {
        /* Partie */
        Partia kpp = new Partia("Komunistyczna Partia Polski");
        Partia pps = new Partia("Polska Paria Socjalistyczna");
        Partia nd = new Partia("Narodowa Demokracja");
        Partia pslPiast = new Partia("Polskie Stronnictwo Ludowe Piast");

        ArrayList<Kandydat> kandydaci = new ArrayList<>();

        /* Kandydaci niezależni */
        kandydaci.add(new KandydatNiezależny("Henryk", "Pobożny", Płeć.MĘŻCZYZNA));
        kandydaci.add(new KandydatNiezależny("Jan", "Olbracht", Płeć.MĘŻCZYZNA));
        kandydaci.add(new KandydatNiezależny("Aleksander", "Koniecpolski", Płeć.MĘŻCZYZNA));
        kandydaci.add(new KandydatNiezależny("Bona", "Sforza", Płeć.KOBIETA));
        kandydaci.add(new KandydatNiezależny("Anna", "Jagiellonka", Płeć.KOBIETA));

        /* Kandydaci KPP */
        kandydaci.add(new KandydatPartyjny("Feliks", "Dzierżyński", Płeć.MĘŻCZYZNA, kpp));
        kandydaci.add(new KandydatPartyjny("Julian", "Marchlewski", Płeć.MĘŻCZYZNA, kpp));
        kandydaci.add(new KandydatPartyjny("Róża", "Luksemburg", Płeć.KOBIETA, kpp));

        /* Kandydaci PPS */
        kandydaci.add(new KandydatPartyjny("Stefan", "Okrzeja", Płeć.MĘŻCZYZNA, pps));
        kandydaci.add(new KandydatPartyjny("Ignacy", "Daszyński", Płeć.MĘŻCZYZNA, pps));
        kandydaci.add(new KandydatPartyjny("Henryk", "Libermann", Płeć.MĘŻCZYZNA, pps));

        /* Kandydaci ND */
        kandydaci.add(new KandydatPartyjny("Roman", "Dmowski", Płeć.MĘŻCZYZNA, nd));
        kandydaci.add(new KandydatPartyjny("Eligiusz", "Niewiadomski", Płeć.MĘŻCZYZNA, nd));

        /* Kandydaci PSL Piast */
        kandydaci.add(new KandydatPartyjny("Wincenty", "Witos", Płeć.MĘŻCZYZNA, pslPiast));
        kandydaci.add(new KandydatPartyjny("Stanisław", "Mikołajczyk", Płeć.MĘŻCZYZNA, pslPiast));

        /* Wyborcy */
        ArrayList<Wyborca> wyborcy = new ArrayList<>();

        for (int j = 0; j < 5; j++)
            wyborcy.add(new Partyjniak(kpp));

        for (int j = 0; j < 4; j++)
            wyborcy.add(new Partyjniak(pps));

        for (int j = 0; j < 6; j++)
            wyborcy.add(new Partyjniak(nd));

        for (int j = 0; j < 5; j++)
            wyborcy.add(new Partyjniak(pslPiast));

        for (int j = 0; j < 5; j++)
            wyborcy.add(new Partyjniak(pslPiast));

        for (int j = 0; j < 7; j++)
            wyborcy.add(new Antysystemowiec());

        for (int j = 0; j < 7; j++)
            wyborcy.add(new Znudzony());

        for (int j = 0; j < 7; j++)
            wyborcy.add(new Feminista());

        Wybory wybory = new Wybory(kandydaci);
        WynikWyborów wynik = wybory.przeprowadźWybory(wyborcy);

        System.out.println(wynik.pokażWynik());


    }
}
