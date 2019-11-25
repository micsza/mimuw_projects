import java.util.*;

class Pomocnicza {
    static ArrayList<Kandydat> dajKandydatówPartii(ArrayList<Kandydat> kandydaci, Partia partia) {
        ArrayList<Kandydat> res = new ArrayList<>();
        for (Kandydat kandydat : kandydaci) {
            if (kandydat.partia() == partia) {
                res.add(kandydat);
            }
        }

        return res;
    }

    static ArrayList<Kandydat> dajKandydatówOPłci(ArrayList<Kandydat> kandydaci, Płeć płeć) {
        ArrayList<Kandydat> res = new ArrayList<>();
        for (Kandydat kandydat : kandydaci) {
            if (kandydat.płeć() == płeć) {
                res.add(kandydat);
            }
        }

        return res;
    }

    static <K, V extends Comparable <? super V>> Map<K, V>
    posortujMapęPoWartościach(final Map<K, V> mapaDoPosortowania) {
        List<Map.Entry<K, V>> lista = new ArrayList<>(mapaDoPosortowania.size());
        lista.addAll(mapaDoPosortowania.entrySet());
        Collections.sort(lista, new Comparator<Map.Entry<K, V>>() {
                    @Override
                    public int compare(final Map.Entry<K, V> entry1, final Map.Entry<K, V> entry2) {
                        return entry2.getValue().compareTo(entry1.getValue());
                    }
                });
        Map<K, V> res = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : lista) {
            res.put(entry.getKey(), entry.getValue());
        }

        return res;
    }
}
