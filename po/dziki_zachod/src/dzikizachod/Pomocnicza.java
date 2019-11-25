package dzikizachod;

import java.util.*;

public class Pomocnicza {
    private static Random rand = new Random();

    static <T> List<T> przecięcie(List<T> lista1, List<T> lista2) {
        List<T> res = new ArrayList<T>();

        for (T t : lista1) {
            if(lista2.contains(t)) {
                res.add(t);
            }
        }

        return res;
    }

    static <T> List<T> wZasięgu(List<T> lista, int indeks, int zasięg) {
        List<T> wZasięguDoTyłu = wZasięguDoTyłu(lista, indeks, zasięg);
        List<T> wZasięguDoPrzodu = wZasięguDoPrzodu(lista, indeks, zasięg);

        return suma(wZasięguDoPrzodu, wZasięguDoTyłu);
    }

    static <T> List<T> wZasięguDoPrzodu(List<T> lista, int indeks, int zasięg) {
        List<T> res = new ArrayList<T>();
        int N = lista.size();
        assert(indeks < N);
        if (N == 0)
            return res;
        int i, j;
        /* dodaj elementy na prawo */
        i = (indeks + 1) % N;
        j = 0;
        while (i != indeks && j < zasięg) {
            res.add(lista.get(i));
            i = (i + 1) % N;
            j++;
        }

        return res;
    }

    private static <T> List<T> wZasięguDoTyłu(List<T> lista, int indeks, int zasięg) {
        List<T> res = new ArrayList<T>();
        int N = lista.size();
        assert (indeks < N);
        if (N == 0)
            return res;
        int i, j;
        /* dodaj elementy na lewo */
        i = (N + indeks - 1) % N;
        j = 0;
        while (i != indeks && j < zasięg ) {
            res.add(lista.get(i));
            i = (N + i - 1) % N;
            j++;
        }

        return res;
    }


    static <T> T wylosuj(List<T> lista) {
        return lista.get(rand.nextInt(lista.size()));
    }

    static <T> List<T> suma(List<T> lista1, List<T> lista2) {
        Set<T> set = new HashSet<T>();

        set.addAll(lista1);
        set.addAll(lista2);

        return new ArrayList<T>(set);
    }

    /* usuwa element z listy jeżeli takowy występuje */
    static <T> void usuń(List<T> lista, T x) {
        Iterator<T> iter = lista.iterator();
        while (iter.hasNext()) {
            T elem = iter.next();
            if (elem == x) {
                iter.remove();
            }
        }
    }

    static void wydrukujListę(List<Widok> lista) {
        System.out.print("<");
        for (Widok widok : lista) {
            System.out.print(widok.toString() + ", ");
        }
        System.out.println(">");
    }
}
