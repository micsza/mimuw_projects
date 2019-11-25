package dzikizachod;

import java.util.ArrayList;
import java.util.Collections;

public class PulaAkcji {
    private ArrayList<KartaAkcji> pula = new ArrayList<>();
    private boolean zawieraDynamit = false;

    public void dodaj(Akcja typ, int liczba) {
        /* liczba <= 0 */
        if (liczba <= 0) {
            System.err.println("Dodanie " + liczba + " kart akcji niemożliwe");
            return;
        }

        /* co najwyżej jeden dynamit */
        if (typ == Akcja.DYNAMIT) {
            if (zawieraDynamit) {
                System.err.println("Nie można dodac dynamitu, gdyż pula już go zawiera");
                return;
            }
            else {
                if (liczba > 1) {
                    System.err.println("Nie można dodac więcej niż jednego dynamitu. Dodaję tylko jeden");
                }
                pula.add(new KartaAkcji(Akcja.DYNAMIT));
                return;
            }
        }

        for (int i = 0; i < liczba; i++)
            pula.add(new KartaAkcji(typ));
    }

    void dodaj(KartaAkcji karta) {
        pula.add(karta);
    }

    public String toString() {
        String s = "pula = [";
        for (KartaAkcji karta : pula)
            s += karta.toString() + ", ";
        s += "]";

        return s;
    }

    boolean czyPusta() {
        return pula.size() == 0;
    }

    int ileKartWPuli() {
        return pula.size();
    }

    KartaAkcji dajKartę() {
        if (this.czyPusta()) {
            return null;
        }

        return pula.remove(0);
    }

    void potasuj() {
        Collections.shuffle(pula);
    }

    public void wydrukujSię() {
        System.out.print("pula (" + ileKartWPuli() + ") = <");
        for (KartaAkcji karta : pula)
            System.out.print(karta.toString() + ", ");
        System.out.println(">");
    }
}
