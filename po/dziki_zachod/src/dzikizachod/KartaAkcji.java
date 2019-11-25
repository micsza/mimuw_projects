package dzikizachod;

public class KartaAkcji {
    private Akcja akcja;
    private int priorytet;

    public KartaAkcji(Akcja akcja) {
        this.akcja = akcja;
        ustawPriorytet();
    }

    public void ustawPriorytet() {
        switch (akcja) {
            case ULECZ:             priorytet = 1;
                                    break;
            case ZASIEG_PLUS_JEDEN: priorytet = 2;
                                    break;
            case ZASIEG_PLUS_DWA:   priorytet = 2;
                                    break;
            case STRZEL:            priorytet = 3;
                                    break;
            case DYNAMIT:           priorytet = 4;
                                    break;
        }

    }

    public Akcja akcja() {
        return this.akcja;
    }

    int priorytet() {
        return priorytet;
    }

    public String toString() {
        return this.akcja.toString();
    }
}
