
public class WymaganieNieChcęMiasta extends Wymaganie {

    public WymaganieNieChcęMiasta(Miasto miasto) {
        super(miasto);
    }

    public boolean czyPlanSpełnia(Plan plan) {
        return !plan.listaMiastDoOdwiedzenia().contains(miasto());
    }

    public String toString() {
        return "wymaganie NIE "
    }
}
