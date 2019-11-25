
public class ZmianaPodmień extends Zmiana {
    private Miasto miastoDoDodania;
    private Miasto miastoDoUsunięcia;

    public ZmianaPodmień(Miasto miastoDoDodania, Miasto miastoDoUsunięcia) {
        this.miastoDoDodania = miastoDoDodania;
        this.miastoDoUsunięcia = miastoDoUsunięcia;
    }

    public Plan zmieńPlan(Plan plan) {
        return dodajMiastoDoPlanu(usuńMiastoZPlanu(plan, miastoDoUsunięcia), miastoDoDodania);
    }
}
