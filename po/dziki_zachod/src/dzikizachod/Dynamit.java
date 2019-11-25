package dzikizachod;

class Dynamit {
    private Widok przedKimLeży;

    Dynamit() {
        przedKimLeży = null;
    }

    Widok przedKimLeży() {
        return przedKimLeży;
    }

    void połóżPrzed(Widok widok) {
        przedKimLeży = widok;
    }

    boolean czyAktywny() {
        return przedKimLeży != null;
    }
}
