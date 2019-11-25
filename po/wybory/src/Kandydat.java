public abstract class Kandydat {
    private String imię;
    private String nazwisko;
    private Płeć płeć;

    public Kandydat(String imię, String nazwisko, Płeć płeć) {
        this.imię = imię;
        this.nazwisko = nazwisko;
        this.płeć = płeć;
    }

    public String imię() {
        return imię;
    }

    public String nazwisko() {
        return nazwisko;
    }

    public Płeć płeć() {
        return płeć;
    }

    public abstract Partia partia();

    public String toString() {
        return imię + " " + nazwisko + "(" + płeć.toString() + ", niezależny)";
    }


}
