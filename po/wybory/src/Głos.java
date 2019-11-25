public class Głos {
    private Kandydat naKogo;

    public Głos(Kandydat naKogo) {
        this.naKogo = naKogo;
    }

    public Kandydat naKogo() {
        return naKogo;
    }

    public String toString() {
        if (naKogo == null)
            return "głos nieważny";

        return naKogo.nazwisko();
    }
}
