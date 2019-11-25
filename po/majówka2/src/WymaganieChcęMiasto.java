
public class WymaganieChcęMiasto extends Wymaganie {


    public WymaganieChcęMiasto(Miasto miasto) {
        super(miasto);
    }

    public boolean czyPlanSpełnia(Plan plan) {
        return plan.listaMiastDoOdwiedzenia().contains(miasto());
    }
}
