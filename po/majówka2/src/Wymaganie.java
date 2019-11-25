
public abstract class Wymaganie {
    private Miasto miasto;

    public Wymaganie(Miasto miasto) {
        this.miasto = miasto;
    }

    public Miasto miasto() {
        return miasto;
    }

    public abstract boolean czyPlanSpełnia(Plan plan);
}
