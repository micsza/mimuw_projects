package dzikizachod;

public class Decyzja {
    public enum RodzajDecyzji {
        ULECZ,
        STRZEL,
        ZWIĘKSZ_ZASIĘG_O_JEDEN,
        ZWIĘKSZ_ZASIĘG_O_DWA,
        RZUĆ_DYNAMIT,
        NIC_NIE_RÓB
    }
    //private int indeks;
    private RodzajDecyzji rodzajDecyzji;
    private Widok kogoDotyczy = null;

    public Decyzja(RodzajDecyzji rodzajDecyzji, Widok widok) {
        this.rodzajDecyzji = rodzajDecyzji;
        this.kogoDotyczy = widok;
    }

    Widok kogoDotyczy() {
        return kogoDotyczy;
    }


    RodzajDecyzji rodzajDecyzji() {
        return rodzajDecyzji;
    }

    public String toString() {
        return rodzajDecyzji.toString();
    }
}
