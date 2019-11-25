public class KandydatPartyjny extends Kandydat {
    private Partia partia;

    public KandydatPartyjny(String imię, String nazwisko, Płeć płeć, Partia partia) {
        super(imię, nazwisko, płeć);
        this.partia = partia;
    }

    public Partia partia() {
        return partia;
    }

    public String toString() {
        return imię() + " " + nazwisko() + "(" + płeć().toString() + ", " + partia().toString() + ")";
    }
}
