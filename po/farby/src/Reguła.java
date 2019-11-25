public class Reguła {
    private Kolor kolorWejściowy;
    private Kolor kolorWyjściowy;
    private Pigment pigment;

    public Reguła(Kolor kolorWejściowy, Kolor kolorWyjściowy, Pigment pigment) {
        this.kolorWejściowy = kolorWejściowy;
        this.kolorWyjściowy = kolorWyjściowy;
        this.pigment = pigment;
    }

    public Kolor kolorWejściowy() {
        return kolorWejściowy;
    }

    public Kolor kolorWyjściowy() {
        return kolorWyjściowy;
    }

    public Pigment pigment() {
        return pigment;
    }

    boolean equals(Reguła reguła) {
        return (this.kolorWejściowy == reguła.kolorWejściowy) && this.pigment.equals(reguła.pigment());
    }

    public String toString() {
        return "<" + pigment.nazwa() + ": " + kolorWejściowy + " => " + kolorWyjściowy + ">";
    }
}
