import java.io.IOException;
import java.util.ArrayList;

public class EmulatorMaszyny {
    private ArrayList<Farba> farby = new ArrayList<>();
    private ArrayList<Pigment> pigmenty = new ArrayList<>();
    private ArrayList<Reguła> reguły = new ArrayList<>();
    private Farba farbaWMieszalniku;
    private static final String etykieta = "[EMULATOR] ";

    public EmulatorMaszyny() {
        farbaWMieszalniku = null;
    }

    public void ustawKonfigurację() {
        Parser parser = new Parser();
        try {
            parser.wczytajScieżkiPlików();
            farby = parser.wczytajFarby();
            pigmenty = parser.wczytajPigmentyIReguły(reguły);
        }
        catch (IOException e) {
            System.out.println(etykieta + "Błąd konfiguracji. Nie można uruchomić maszyny. ");
        }
        System.out.println(etykieta + "Moja konfiguracja:");
        String odstęp = "    ";
        System.out.println(odstęp + "farby = "  + farby);
        System.out.println(odstęp + "pigmenty = "  + pigmenty);
        System.out.println(odstęp + "reguły = "  + reguły);

    }

    public Farba farbaWMieszalniku() {
        return farbaWMieszalniku;
    }

    void ustawFarbęWMieszalniku(Farba farba) {
        // todo bład jesli nowaFarbaWynikowa nie nalezy do farb
        assert(farby.contains(farba));
        farbaWMieszalniku = farba.dajPróbkę();
        System.out.println(etykieta + "Ustawiono farbę w mieszalniku: " + farbaWMieszalniku);
    }

    void ustawFarbęWMieszalniku() {
        // todo bład jesli nowaFarbaWynikowa nie nalezy do farb
        assert(farby.size() > 0);
        farbaWMieszalniku = farby.get(0).dajPróbkę();
        System.out.println(etykieta + "Ustawiono farbę w mieszalniku: " + farbaWMieszalniku);
    }

    boolean czyZawieraFarbę(Farba nowaFarba) {
        for (Farba farba : farby) {
            /* Jest co najwyżej jeden obiekt o danym kolorze więc nie trzeba metody equals() a zwykle ==
             */
            if (farba.kolor() == nowaFarba.kolor())
                return true;
        }
        return false;
    }

    boolean czyZawieraPigment(Pigment nowyPigment) {
        for (Pigment pigment : pigmenty) {
            if (nowyPigment.equals(pigment))
                return true;
        }
        return false;
    }

    boolean czyZwieraRegułę(Reguła nowaReguła) {
        for (Reguła reguła : reguły) {
            if (nowaReguła.equals(reguła))
                return true;
        }
        return false;
    }

    boolean dodajFarbę(Farba nowaFarba) {
        if (czyZawieraFarbę(nowaFarba))
            return false;

        farby.add(nowaFarba);
        return true;
    }

    boolean dodajPigment(Pigment nowyPigment) {
        if (czyZawieraPigment(nowyPigment))
            return false;

        pigmenty.add(nowyPigment);
        return true;
    }

    boolean dodajRegułę(Reguła reguła) {
        if (czyZwieraRegułę(reguła))
            return false;
        reguły.add(reguła);
        return true;
    }

    boolean mieszajFarbę(Pigment pigment) {
        System.out.print(etykieta + "Zaczynam mieszanie ...");
        /* Znajdż regułę właściwą dla farby w mieszalniku oraz pigmentu */
        if (farbaWMieszalniku == null) {
            System.out.println("ooops, brak farby w mieszalniku.");
            return false;
        }
        System.out.println("farba w mieszalniku: " + farbaWMieszalniku.toString());

        Reguła właściwaReguła = null;
        for (Reguła reguła : reguły) {
            if ((reguła.pigment().equals(pigment)) && (reguła.kolorWejściowy() == farbaWMieszalniku.kolor())) {
                właściwaReguła = reguła;
                System.out.println(etykieta + "Znaleziono właściwą regułę: " + właściwaReguła.toString());
                break;
            }
        }

        if (właściwaReguła == null) {
            System.out.println(etykieta + "Brak właściwej reguły dla farby " + farbaWMieszalniku.toString() + " oraz pigmentu "
                    + pigment.toString());
            return false;
        }

        farbaWMieszalniku.mieszajSięWg(właściwaReguła);
        System.out.println(etykieta + "Nowa farba w mieszalniku: " + farbaWMieszalniku.toString());
        return true;
    }

    public ArrayList<Pigment> pigmenty() {
        return pigmenty;
    }
}
