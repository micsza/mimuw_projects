import java.util.ArrayList;
import java.lang.*;
import java.util.Random;

/*
 ******************* Warzywo *******************
 */

abstract class Warzywo {
    protected double kosztPosadzenia; // poprawic zeby to byla const czyli static final
    protected long czasPosadzenia;
    protected long czasZebrania;

    Warzywo(int koszt) {
        czasPosadzenia = System.currentTimeMillis();
        kosztPosadzenia = koszt;
        czasZebrania = -1;
    }

    protected long dlugośćWegetacji() {
        return System.currentTimeMillis() - czasPosadzenia;
    }

    public double kosztPosadzenia() {
        return kosztPosadzenia;
    }

    public abstract double wartość();

    public String toString() {
        return getClass().getName() + "(wart = " + this.wartość() + ") ";

    }
}

class Ziemniak extends Warzywo {

    private static final int KOSZT = 5;
    private static final long CZAS_DOJRZEWANIA = 10000;
    private static final int WARTOŚĆ_ZIEMNIAKA = 10;

    public Ziemniak() {
        super(KOSZT);
        //kosztPosadzenia = koszt;
    }

    @Override
    public double wartość() {
        if (dlugośćWegetacji() < CZAS_DOJRZEWANIA)
            return 0;
        else
            return WARTOŚĆ_ZIEMNIAKA;
    }
}

class Pomidor extends Warzywo {

    private static final int KOSZT_POSADZENIA = 8;
    private static final long CZAS_DOJRZEWANIA = 5000;
    private static final long CZAS_DO_WARTOŚCI_MAX = 12000;
    private static final long CZAS_DO_ZGNICIA = 9000;
    private static final int WARTOŚĆ_MAX = 20;
    private static final double NACHYLENIE_WZROSTU_WARTOŚCI = (double) WARTOŚĆ_MAX / CZAS_DO_WARTOŚCI_MAX;
    private static final double NACHYLENIE_SPADKU_WARTOŚCI = (double) WARTOŚĆ_MAX / CZAS_DO_ZGNICIA;


    public Pomidor() {
        super(KOSZT_POSADZENIA);
    }

    @Override
    public double wartość() {
        long t = dlugośćWegetacji();

        if (t < CZAS_DOJRZEWANIA)
            return 0;
        if (t < CZAS_DO_WARTOŚCI_MAX)
            return NACHYLENIE_WZROSTU_WARTOŚCI * t;

        double res = WARTOŚĆ_MAX - (NACHYLENIE_SPADKU_WARTOŚCI * t);
        return (res > 0 ? res : 0);
    }
}

class Ogórek extends Warzywo {
    private static final int KOSZT_POSADZENIA = 6;
    private static final long CZAS_DOJRZEWANIA = 2500;
    private static final long CZAS_DO_MAX = 5000;
    private static final long WARTOŚĆ_MAX = 14;
    private static final long CZAS_DO_PÓŁ_MAX = 7000;
    private static final long WARTOŚĆ_PÓŁ_MAX = 7;

    public Ogórek() {
        super(KOSZT_POSADZENIA);
    }

    @Override
    public double wartość() {
        long t = dlugośćWegetacji();

        if (t < CZAS_DOJRZEWANIA)
            return 0;
        if (t < CZAS_DO_MAX)
            return WARTOŚĆ_MAX;
        if (t < CZAS_DO_PÓŁ_MAX)
            return WARTOŚĆ_PÓŁ_MAX;

        return 0;
    }

}

/*
 ******************* Ogród  *******************
 */

class Ogród {
    private Warzywo[] pole;
    private int rozmiar;
    public static int ILOŚĆ_WARZYW = 3;

    Ogród(int rozmiarOgrodu) {
        this.rozmiar = rozmiarOgrodu;
        pole = new Warzywo[this.rozmiar];
    }

    int rozmiar() {
        return rozmiar;
    }

    public void przyjmijWarzywo(Warzywo warzywo, int k) {
        pole[k] = warzywo;
    }

    public Warzywo wyjmijWarzywo(int k) {
        Warzywo w = pole[k];
        pole[k] = null;
        return w;
    }

    public Warzywo pole(int k) {
        return pole[k];
    }
}

/*
 ******************* Rolnik  *******************
 */

abstract class Rolnik {

    private String imię;
    private ArrayList<Warzywo> posadzone;
    private ArrayList<Warzywo> zebrane;
    private Ogród ogród;
    private int przychody;
    private int koszty;
    long początekPracy;
    long początekŻycia;

    protected int koszty() {
        return koszty;
    }

    protected int przychody() {
        return przychody;
    }

    public Rolnik(String imię) {
        this.imię = imię;
        przychody = 0;
        koszty = 0;
        początekPracy = -1;
        zebrane = new ArrayList<Warzywo>();
    }

    public Rolnik(String imię, int rozmiarOgrodu) {

        this.imię = imię;
        this.ogród = new Ogród(rozmiarOgrodu);
        przychody = 0;
        koszty = 0;
        zebrane = new ArrayList<Warzywo>();
    }

    public String imię() {
        return imię;
    }

    public Ogród ogród() {
        return ogród;
    }

    protected static Warzywo losujWarzywo() {

        Random rnd = new Random();
        int kodWarzywa = rnd.nextInt(Ogród.ILOŚĆ_WARZYW);
        if (kodWarzywa == 0)
            return new Ziemniak();
        if (kodWarzywa == 1)
            return new Pomidor();
        if (kodWarzywa == 2)
            return new Ogórek();

        return new Ziemniak();
    }

    protected void posadźWszystko() {
        for (int i = 0; i < this.ogród().rozmiar(); i++) {
            this.posadźWarzywo(losujWarzywo(), i);
        }
    }

    protected void zbierzWszystko() {
        for (int i = 0; i < this.ogród().rozmiar(); i++) {
            Warzywo warzywo = this.zbierzWarzywo(i);
        }
    }

    protected void posadźWarzywo(Warzywo warzywo, int k) {
        this.ogród().przyjmijWarzywo(warzywo, k);
        koszty += warzywo.kosztPosadzenia();
        System.out.println(kto() + " posadziłem " + this.ogród().pole(k).getClass().getName() + " na miejsce " + k);
    }

    protected Warzywo zbierzWarzywo(int k) {
        Warzywo warzywo = this.ogród().wyjmijWarzywo(k);
        przychody += warzywo.wartość();
        zebrane.add(warzywo);
        System.out.println(kto() + " zebrałem " + warzywo.getClass().getName() + " z miejsca " + k);

        return warzywo;
    }

    protected void czekaj(int ileSekundCzekaj) {
        System.out.println(kto() + " czekam " + ileSekundCzekaj + " s");
        try {
            // thread to sleep for 1000 milliseconds
            Thread.sleep(1000 * ileSekundCzekaj);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    protected void podsumuj() {
        System.out.println(this.kto() + " \n moje koszty = " + this.koszty() +
                ", moje przychody = " + this.przychody() + " => mój zysk = " + (this.przychody() - this.koszty()));
        System.out.println("zebrane[] = " + zebrane.toString());
    }

    protected String kto(long momentPoczątkowy) {
        return "[" + czas(momentPoczątkowy) + "] " + this.imię() + "(" + this.getClass().getName() +
                "): ";
    }

    protected String kto() {
        return "[" + czas() + "] " + this.imię() + "(" + this.getClass().getName() +
                "): ";
    }

    static long czas(long momentPoczątkowy) {
        return System.currentTimeMillis() - momentPoczątkowy;
    }

    protected long czas() {
        return System.currentTimeMillis() - this.początekPracy;
    }

    //abstract void pracuj(Ogród ogród, int ileCzasu);
    abstract void pracuj(int ileCzasu);

    public static void main(String[] args) {

        PracownikPGR rolnik_1 = new PracownikPGR("Kazio", 10, 5);
        //rolnik_1.pracuj(30);

        Gospodarz rolnik_2 = new Gospodarz("Janusz", 1, 7);
        rolnik_2.pracuj(30);
    }
}

class PracownikPGR extends Rolnik {
    private int częstotliwośćPracy;

    public PracownikPGR(String imię, int częstotliwośćPracy, int rozmiarOgrodu) {
        super(imię, rozmiarOgrodu);
        this.częstotliwośćPracy = częstotliwośćPracy;
    }

    void pracuj(int ileCzasu) {
        początekPracy = System.currentTimeMillis();

        System.out.println("[" + czas() + "] " + this.imię() + "(" + this.getClass().getName() +
                "): dzień dobry, zaczynam pracę!");
        while (czas() < 1000 * ileCzasu) {
            posadźWszystko();
            czekaj(7);
            zbierzWszystko();
        }

        podsumuj();
    }
}

class Gospodarz extends Rolnik {
    private int częstotliwośćPracy;
    private double[] rejestrWartości;

    public Gospodarz(String imię, int częstotliwośćPracy, int rozmiarOgrodu) {
        super(imię, rozmiarOgrodu);
        this.częstotliwośćPracy = częstotliwośćPracy;
        rejestrWartości = new double[rozmiarOgrodu];
        for (int i = 0; i < rozmiarOgrodu; i++)
            rejestrWartości[i] = -1;
    }

    void przejrzyjOgród() {
        for (int i = 0; i < this.ogród().rozmiar(); i++) {
            double wartośćWarzywa = this.ogród().pole(i).wartość();
            if (wartośćWarzywa < rejestrWartości[i]) {
                Warzywo warzywo = this.zbierzWarzywo(i);
                this.posadźWarzywo(losujWarzywo(), i);
                rejestrWartości[i] = -1;
            }
            else {
                rejestrWartości[i] = wartośćWarzywa;
            }
        }

    }
    void pracuj(int ileCzasu) {
        początekPracy = System.currentTimeMillis();

        System.out.println("[" + czas() + "] " + this.imię() + "(" + this.getClass().getName() +
                "): dzień dobry, zaczynam pracę!");

        posadźWszystko();
        while (czas() < 1000 * ileCzasu) {
            czekaj(częstotliwośćPracy);
            przejrzyjOgród();
        }
        zbierzWszystko();
        podsumuj();
    }
}
