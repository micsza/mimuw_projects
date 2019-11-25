import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NiepoprawnaDefinicjaFarby extends Exception {}

class KlasaLokalne {}

public class Parser {
    private final static String PLIK_STARTOWY = "maszyna.txt";
    private final static String WZÓR_FARBY = "([a-zA-Z]\\w+);(\\d+);(\\d+)";
    private final static String WZÓR_PIGMENTU =
            "(\\w+);([a-zA-Z]\\w+);([a-zA-Z]\\w+);([+-x])([0-9]+(\\.[0-9]+)?);([+-x])([0-9]+(\\.[0-9]+)?)";
    private static final Pattern REGEX_FARBY = Pattern.compile(WZÓR_FARBY);
    private static final Pattern REGEX_PIGMENTU = Pattern.compile(WZÓR_PIGMENTU);
    private String plikFarb;
    private String plikPigmentów;
    private static final String etykieta = "[PARSER] ";

    /*
    public static Farba parsujFarbę(String linijka) throws NiepoprawnaDefinicjaFarby {

    }
    */

    public void wczytajScieżkiPlików() throws IOException {
        String line;
        // todo ulepszyc, wczytujemy tylko 2 linijki a nie caly plik
        List<String> linijki = Files.readAllLines(Paths.get(PLIK_STARTOWY), Charset.forName("UTF-8"));
        for(String linijka :linijki){
            System.out.println(linijka);
        }
        plikFarb = linijki.get(0);
        plikPigmentów = linijki.get(1);
    }

    //todo nie parsuje poprawnie polskich znaków
    public ArrayList<Farba> wczytajFarby() throws IOException {
        File plik = new File(plikFarb);
        // todo do zmiany, bo brzydkie
        if (!plik.isFile()) {
            throw new IOException();
        }
        List<String> linijki = Files.readAllLines(Paths.get(plikFarb), Charset.forName("UTF-8"));
        ArrayList<Farba> res = new ArrayList<>();
        Matcher matcher;
        for(String linijka :linijki){
            //System.out.println(etykieta + "Wczytałem linijkę: " + linijka);
            matcher = REGEX_FARBY.matcher(linijka);
            if (!matcher.matches())
                System.out.println("Błąd odczytu farby dla linijki: " + linijka);
            else {
                String nazwa = matcher.group(1);
                int jakość = Integer.parseInt(matcher.group(3));
                if (jakość < 0 || jakość > 100) {
                    System.out.println("Jakość poza widełkami");
                    continue;
                }
                int toksyczność = Integer.parseInt(matcher.group(2));
                if (toksyczność < 0 || toksyczność > 100) {
                    System.out.println("Toksyczność poza widełkami");
                    continue;
                }
                res.add(new Farba(Kolor.dajKolor(nazwa), (double) jakość, (double) toksyczność));

            }
        }
        System.out.println(etykieta + "Wczytałem farby: " + res.toString());

        return res;
    }

    public ArrayList<Pigment> wczytajPigmentyIReguły(ArrayList<Reguła> reguły) throws IOException {
        File plik = new File(plikPigmentów);
        if (!plik.isFile()) {
            throw new IOException();
        }
        List<String> linijki = Files.readAllLines(Paths.get(plikPigmentów), Charset.forName("UTF-8"));
        ArrayList<Pigment> res = new ArrayList<>();
        Matcher matcher;
        for(String linijka :linijki){
            //System.out.println(etykieta + "Wczytałem linijkę: " + linijka);
            matcher = REGEX_PIGMENTU.matcher(linijka);
            if (!matcher.matches())
                System.out.println(etykieta + "Błąd odczytu pigmentu dla linijki: " + linijka);
            else {
                String nazwa = matcher.group(1);
                Kolor kolorWej = Kolor.dajKolor(matcher.group(2));
                Kolor kolorWyj = Kolor.dajKolor(matcher.group(3));
                /*
                System.out.println("grupa 4 = " + matcher.group(4));
                System.out.println("grupa 5 = " + matcher.group(5));
                System.out.println("grupa 6 = " + matcher.group(6));
                System.out.println("grupa 7 = " + matcher.group(7));
                System.out.println("grupa 8 = " + matcher.group(8));
                System.out.println("grupa 9 = " + matcher.group(9));
                */
                RodzajModyfikatora rodzajModyfikatoraJakości;
                RodzajModyfikatora rodzajModyfikatoraToksyczności;

                if (matcher.group(4).equals("x"))
                    rodzajModyfikatoraToksyczności = RodzajModyfikatora.RAZY;
                else
                    rodzajModyfikatoraToksyczności = RodzajModyfikatora.PLUS;


                double modyfikatorToksyczności = Double.parseDouble(matcher.group(5));

                if (matcher.group(7).equals("x"))
                    rodzajModyfikatoraJakości = RodzajModyfikatora.RAZY;
                else
                    rodzajModyfikatoraJakości = RodzajModyfikatora.PLUS;

                double modyfikatorJakości = Double.parseDouble(matcher.group(8));

                Pigment pigment = Pigment.dajPigment(nazwa, rodzajModyfikatoraJakości, modyfikatorJakości,
                        rodzajModyfikatoraToksyczności, modyfikatorToksyczności);
                Reguła reguła = new Reguła(kolorWej, kolorWyj, pigment);

               res.add(pigment);
               reguły.add(reguła);
               //System.out.println(etykieta + "Dodano pigment " + pigment.toString() + " oraz regułę " +
                //       reguła.toString());
            }
        }
        System.out.println(etykieta + "Wczytałem pigmenty: " + res.toString());

        return res;
    }

    public void wczytajŚcieżkiPlików2() throws IOException {
        File plik = new File(PLIK_STARTOWY);
        System.out.println(plik.isFile());
        System.out.println(plik.exists());

                /*
        try (
                InputStream fis = new FileInputStream("the_file_name");
                InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                BufferedReader br = new BufferedReader(isr);
        ) {
            while ((line = br.readLine()) != null) {
                // Deal with the line
            }
        }
        */
    }
}
