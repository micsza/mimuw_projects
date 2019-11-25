/** @file
   Implementacja klasy wielomianów

   @author Michał Szafraniuk
   @copyright Uniwersytet Warszawski
   @date 2017-05-13
*/

#include <stdio.h>
#include "poly.h"

/* forward declarations of the helper functions */
static void RemoveNode(PolyPtr r, PolyNodePtr node);
static void AppendAsFirst(PolyPtr r, PolyNodePtr node);
static void AppendAsLast(PolyPtr r, PolyNodePtr node);
static bool MonoIsEq(const Mono *m1, Mono *m2);
static Mono MonoNeg(const Mono *m);
static void CopyTail(PolyPtr r, const PolyNode *p_iter);
static Mono MonoMul(const Mono *m1, const Mono *m2);
static poly_exp_t PolyLength(const Poly *p);
static inline bool MonoExpZero(const Mono *m);

Poly PolyClone(const Poly *p) {
    assert(p);
    /* jeśli wielomian jest stały to zwracamy strukturę bez dowiązań */
    if (PolyIsCoeff(p)) {
        return PolyFromCoeff(p->const_coeff);
    }
    Poly r;
    PolyNodePtr p_iter = p->first;
    /* tu wielomian nie jest stały więc bedzie co najmniej jeden wezel */
    PolyNodePtr r_iter = (PolyNodePtr) malloc(sizeof(PolyNode));
    assert(r_iter);
    r.first = r_iter;
    r_iter->prev = NULL;
    r_iter->mono = MonoClone(&p_iter->mono);
    while (p_iter->next != NULL) {
        r_iter->next = (PolyNodePtr) malloc(sizeof(PolyNode));
        assert(r_iter->next);
        r_iter->next->prev = r_iter;
        r_iter->next->mono = MonoClone(&p_iter->next->mono);
        p_iter = p_iter->next;
        r_iter = r_iter->next;
    }
    r.last = r_iter;
    r_iter->next = NULL;

    return r;
}

void PolyDestroy(Poly *p) {
    assert(p);
    if (PolyIsCoeff(p)) {
        return;
    }
    PolyNodePtr p_iter, node;
    p_iter = p->first;
    while (p_iter != NULL) {
        MonoDestroy(&p_iter->mono);
        node = p_iter;
        p_iter = p_iter->next;
        free(node);
    }
}

Poly PolyAdd(const Poly *p, const Poly *q) {
    assert(q);
    assert(p);

    /* przypadek 0: zerowy + dowolny */
    if (PolyIsZero(p)) {
        return PolyClone(q);
    }
    if (PolyIsZero(q)) {
        return PolyClone(p);
    }

    /* przypadek 1: stały + stały */
    if (PolyIsCoeff(q) && PolyIsCoeff(p)) {
        return PolyFromCoeff(q->const_coeff + p->const_coeff);
    }

    /* przypadek 2: stały + normalny */
    if (PolyIsCoeff(q) || PolyIsCoeff(p)) {
        Poly coeff, noncoeff;

        if (PolyIsCoeff(q)) {
            coeff = PolyClone(q);
            noncoeff = PolyClone(p);
        }
        else {
            coeff = PolyClone(p);
            noncoeff = PolyClone(q);
        }

        /* wielomian normalny ma jednomian o wykładniku zero
         * => dodajemy stały do współczynnika tego jednomianu */
        if (noncoeff.first->mono.exp == 0) {
            Poly new_added_poly = PolyAdd(&noncoeff.first->mono.p, &coeff);
            /* może powstać wielomian zerowy, wtedy usuwamy taki jednomian */
            if (PolyIsZero(&new_added_poly)) {
                /* nie dodajemy */
                RemoveNode(&noncoeff, noncoeff.first);
            }
            else {
                PolyDestroy(&noncoeff.first->mono.p);
                noncoeff.first->mono.p = new_added_poly;
            }
        }
        else {
            /* wielomian normalny nie ma jednomianu z wykładnikiem zero
             * => nie może powstać wielomian zerowy =>
             * doklejamy do niego wielomian stały jako jednomian o wykładniku
             * zero */
            PolyNodePtr new_node = (PolyNodePtr) malloc(sizeof(PolyNode));
            assert(new_node);
            new_node->mono = MonoFromPoly(&coeff, 0);
            AppendAsFirst(&noncoeff, new_node);
        }
        PolyDestroy(&coeff);

        return noncoeff;
    }

    /* przypadek 3 - co najmniej jeden wezel lub wielomian zerowy*/
    Poly r = (Poly) {.first = NULL, .const_coeff = 0};
    PolyNodePtr new_node;
    const PolyNode *q_iter = q->first;
    const PolyNode *p_iter = p->first;
    /* co najmniej raz wchodzimy do tej petli */
    while ((q_iter != NULL) && (p_iter != NULL)) {
        new_node = (PolyNodePtr) malloc(sizeof(PolyNode));
        assert(new_node);
        if (q_iter->mono.exp == p_iter->mono.exp) {
            /* równe wykładniki => może powstać jednomian zerowy */

            Poly s = PolyAdd(&q_iter->mono.p, &p_iter->mono.p);
            new_node->mono = MonoFromPoly(&s, q_iter->mono.exp);
            q_iter = q_iter->next;
            p_iter = p_iter->next;
        }
        else {
            /* różne wykładniki => nie może powstać jednomian zerowy */
            if (q_iter->mono.exp < p_iter->mono.exp) {
                new_node->mono = MonoClone(&(q_iter->mono));
                q_iter = q_iter->next;
            }
            else {
                new_node->mono = MonoClone(&(p_iter->mono));
                p_iter = p_iter->next;
            }
        }
        /* doklejamy tylko jesli powstał jednomian niezerowy */
        if (!(MonoIsZero(&new_node->mono))) {
            AppendAsLast(&r, new_node);
        }
        else {
            free(new_node);
        }
    }

    /* mogła zostać koncowka z ktoregos wielomianu */
    if (q_iter != NULL || p_iter != NULL) {
        if (p_iter == NULL) {
            p_iter = q_iter;
        }
        CopyTail(&r, p_iter);
    }

    /* sprawdzamy czy nie powstał nielegalny wielomian stały */
    if ((!PolyIsCoeff(&r)) && (r.first == r.last) &&
        MonoIsCoeff(&r.first->mono)) {
        poly_coeff_t c = r.first->mono.p.const_coeff;
        PolyDestroy(&r);
        return PolyFromCoeff(c);
    }

    /* jeśli r jest NULLem to wszystkie jednomiany sie wyzerowaly */
    if (r.first == NULL) {
        return PolyZero();
    }

    return r;
}

Poly PolyAddMonos(unsigned count, const Mono monos[]) {
    assert(count > 0);
    unsigned i, k;

    /* przypadek szczególny:  w monos same jednomiany stałe o
     * wspólczynnikach stałych */
    k = 0;
    poly_coeff_t sum = 0;
    while ((k < count) && (MonoIsCoeff((Mono* const) &monos[k]))) {
        sum += monos[k].p.const_coeff;
        k++;
    }
    if (k == count) {
        return PolyFromCoeff(sum);
    }

    Poly r;
    r.first = NULL;
    r.last = NULL;
    PolyNodePtr iter, new_node;
    /* przechodzimy po monos i wklejamy nowy wezel do Poly ew update'ujemy */
    for (i = 0; i < count; i++) {
        /* zignoruj jednomian zerowy */
        if (MonoIsZero(&monos[i]))
            continue;

        /* znajdz m-ce do wstawienia jednomianu */
        iter = r.first;
        while (iter != NULL && iter->mono.exp < monos[i].exp) {
            iter = iter->next;
        }
        /* wstawiamy na koniec */
        if (iter == NULL) {
            new_node = (PolyNodePtr) malloc(sizeof(PolyNode));
            assert(new_node);
            new_node->mono = monos[i];
            AppendAsLast(&r, new_node);
        }
        else {
            /* iter nie jest NULLem wiec update'ujemy lub wstawiamy w srodek */
            /* powtarzajcy się wykładnik => dodajemy wielomiany */
            if (iter->mono.exp == monos[i].exp) {
                /* tu może powstać wielomian zerowy */
                Poly new_poly = PolyAdd(&iter->mono.p, &(monos[i].p));
                if (PolyIsZero(&new_poly)) {
                    /* powstał wielomian zerowy => usuwamy wezel iter
                     * z wielomianu wynikowego */
                    RemoveNode(&r, iter);
                    if (PolyIsCoeff(&r)) {
                        r.const_coeff = 0;
                    }
                    PolyDestroy(&new_poly);
                }
                else {
                    PolyDestroy(&iter->mono.p);
                    iter->mono.p = new_poly;
                }
                MonoDestroy((void*)&monos[i]);
            }
            else { /* wstawiamy przed iter */
                new_node = (PolyNodePtr) malloc(sizeof(PolyNode));
                assert(new_node);
                new_node->mono = monos[i];
                new_node->prev = iter->prev;
                new_node->next = iter;
                iter->prev = new_node;
                /* iter wskazuje na wezel przed ktory chcemy wpiac */
                if (new_node->prev == NULL) /* wpielismy na poczatek */
                    r.first = new_node;
                else
                    new_node->prev->next = new_node;
            }
        }
    }

    return r;
}

Poly PolyMul(const Poly *p, const Poly *q) {
    assert(p);
    assert(q);

    /* przypadek specjalny - mnożenie przez wielomian zerowy */
    if (PolyIsZero(p) || PolyIsZero(q)) {
        return PolyZero();
    }

    /* przypadek bazowy - stały * stały */
    if (PolyIsCoeff(p) && PolyIsCoeff(q)) {
        return PolyFromCoeff(p->const_coeff * q->const_coeff);
    }


    /* zliczamy ilość składników w wielomianach
     * licznik równy 1 oznacza wielomian stały lub wielomian
     * będący jednomianem */
    unsigned int np = 0;
    unsigned int nq = 0;
    PolyNodePtr p_iter, q_iter;
    if (PolyIsCoeff(p)) {
        np = 1;
    }
    else {
        p_iter = p->first;
        while (p_iter != NULL) {
            np++;
            p_iter = p_iter->next;
        }
    }
    if (PolyIsCoeff(q)) {
        nq = 1;
    }
    else {
        q_iter = q->first;
        while (q_iter != NULL) {
            nq++;
            q_iter = q_iter->next;
        }
    }

    /* alokujemy dynamicznie tablice jednomianow aby uniknac przepelnienia
     * stosu */
    Mono *monos;
    monos = (Mono*) malloc(np * nq * sizeof(Mono));
    assert(monos);

    /* przypadek specjalny: stały * normalny */
    if (PolyIsCoeff(p) || PolyIsCoeff(q)) {
        /* z wielomianu stałego tworzymy jednomian stały */
        PolyNodePtr iter;
        Poly poly_tmp;
        Mono mono_tmp;
        if (PolyIsCoeff(p)) {
            iter = q->first;
            poly_tmp = PolyClone(p);
        }
        else {
            iter = p->first;
            poly_tmp = PolyClone(q);
        }
        mono_tmp = MonoFromPoly(&poly_tmp, 0); // jednomian stały

        /* wymnażamy utworzony jednomian stały z jednomianami drugiego
         * wielomianu*/
        int j = 0;
        while (iter != NULL) {
            monos[j++] = MonoMul(&iter->mono, &mono_tmp);
            iter = iter->next;
        }
        MonoDestroy(&mono_tmp);
    }
    else { /* oba wielomiany normalne */
        p_iter = p->first;
        unsigned j = 0;
        while (p_iter != NULL) {
            q_iter = q->first;
            while (q_iter != NULL) {
                assert(j < np * nq);
                monos[j] = MonoMul(&q_iter->mono, &p_iter->mono);
                q_iter = q_iter->next;
                j++;
            }
            p_iter = p_iter->next;
        }
    }
    /* na wyjsciu z tej galezi mamy poprawna tablica monos[] */
    Poly res = PolyAddMonos(np*nq, monos);
    free(monos);

    return res;
}

Poly PolyNeg(const Poly *p) {
    assert(p);

    /* przypadek bazowy: wielomian stały */
    if (PolyIsCoeff(p)) {
        return PolyFromCoeff(-1 * p->const_coeff);
    }

    /* przypadek rekurencyjny: wielomian normalny */
    Poly r = (Poly) {.first = NULL, .last = NULL};
    PolyNodePtr p_iter = p->first;
    /* co najmniej raz wchodzimy do pętli */
    while (p_iter != NULL) {
        PolyNodePtr new_node = (PolyNodePtr) malloc(sizeof(PolyNode));
        assert(new_node);
        new_node->mono = MonoNeg(&p_iter->mono);
        AppendAsLast(&r, new_node);
        p_iter = p_iter->next;
    }

    return r;
}

Poly PolySub(const Poly *p, const Poly *q) {
    assert(p);
    assert(q);
    Poly q_neg = PolyNeg(q);
    Poly r = PolyAdd(p, &q_neg);
    PolyDestroy(&q_neg);

    return r;
}

poly_exp_t PolyDeg(const Poly *p) {
    assert(p);
    if (p->first == NULL)
        return (p->const_coeff == 0 ? -1 : 0);
    PolyNodePtr p_iter = p->first;
    poly_exp_t deg_max, deg;
    deg_max = 0;
    while (p_iter != NULL) {
        deg = p_iter->mono.exp + PolyDeg(&p_iter->mono.p);
        if (deg > deg_max)
            deg_max = deg;
        p_iter = p_iter->next;
    }

    return deg_max;
}

bool PolyIsEq(const Poly *p, const Poly *q) {
    assert(p);
    assert(q);

    if (PolyIsCoeff(p) && PolyIsCoeff(q))
        return (p->const_coeff == q->const_coeff);

    if (PolyIsCoeff(p) || PolyIsCoeff(q))
        return false;

    PolyNodePtr p_iter = p->first;
    PolyNodePtr q_iter = q->first;

    while (p_iter != NULL && q_iter != NULL) {
        if (!MonoIsEq(&p_iter->mono, &q_iter->mono))
            return false;
        p_iter = p_iter->next;
        q_iter = q_iter->next;
    }

    if (p_iter != NULL || q_iter != NULL)
        return false;

    return true;
}

Poly PolyAt(const Poly *p, poly_coeff_t x) {
    assert(p);

    /* przypadek bazowy - wielomian stały */
    if (PolyIsCoeff(p)) {
        return PolyClone(p);
    }

    /* przypadek specjalny x = 0 */
    if (x == 0) {
        if (MonoExpZero(&(p->first->mono)))
            return PolyClone(&(p->first->mono.p));
        else
            return PolyZero();
    }

    /* zliczamy ilość jednomianów niższego rzędu by znac rozmiar tablicy */
    unsigned n = 0;
    PolyNodePtr p_iter;
    p_iter = p->first;
    while (p_iter != NULL) {
        n += PolyLength(&p_iter->mono.p);
        p_iter = p_iter->next;
    }
    Mono monos[n];
    int j = 0;
    p_iter = p->first;
    while (p_iter != NULL) {
        poly_coeff_t t = (poly_coeff_t) pow(x, p_iter->mono.exp);
        if (PolyIsCoeff(&p_iter->mono.p)) {
            monos[j++] = MonoFromCoeff(t * p_iter->mono.p.const_coeff, 0);

        }
        else {
            Mono m = MonoFromCoeff(t, 0);
            PolyNodePtr iter = p_iter->mono.p.first;
            while (iter != NULL) {
                monos[j++] = MonoMul(&iter->mono, &m);
                iter = iter->next;
            }
        }
        p_iter = p_iter->next;
    }

    return PolyAddMonos(n, monos);
}

poly_exp_t PolyDegBy(const Poly *p, unsigned var_idx) {
    if (PolyIsZero(p))
        return -1;
    if (PolyIsCoeff(p))
        return 0;

    poly_exp_t deg_max = 0;
    poly_exp_t deg;

    PolyNodePtr iter = p->first;
    while (iter != NULL) {
        if (var_idx == 0) {
            if (iter->mono.exp > deg_max)
                deg_max = iter->mono.exp;
        }
        else {
            deg = PolyDegBy(&iter->mono.p, var_idx - 1);
            if (deg > deg_max)
                deg_max = deg;
        }
        iter = iter->next;
    }

    return deg_max;
}

/**
 * zwraca długość wielomianu równą ilości jednomianów go tworzących dla
 * wielomianu normalnego oraz 1 dla wielomianu stałego. Długość ta jest
 * niewiększa od stopnia wielomianu dlatego zwracamy `poly_exp_t`
 * @param[in] p : wielomian
 * @return długość wielomianu
 */
static int PolyLength(const Poly *p) {
    assert(p);
    if (PolyIsCoeff(p))
        return 1;

    int res = 0;
    PolyNodePtr iter = p->first;
    while (iter != NULL) {
        res++;
        iter = iter->next;
    }

    return res;
}

/**
 * sprawdza czy jednomian ma wykładnik zero (jego współczynnik może być
 * dowolnym wielomianem niezerowym)
 * @param[in] m : jednomian
 * @return Czy jednomian ma wykładnik zerowy
 * */
static inline bool MonoExpZero(const Mono *m) {
    return (m->exp == 0);
}

/**
 * Usuwa węzeł wskazywany przez `node` z wielomianu
 * @param[out] r : wielomian
 * @param[in] node : węzeł do wycięcia
 */
static void RemoveNode(PolyPtr r, PolyNodePtr node) {
    assert(r);
    assert(node);
    assert(!PolyIsCoeff(r));

    /* node jest pierwszy */
    if (r->first == node) {
        /* pierwszy i ostatni */
        if (r->last == node) {
            r->first = NULL;
            //r->last = NULL;
            r->const_coeff = -1;
        }
        else { /* pierwszy, ale nie ostani */
            r->first = node->next;
            r->first->prev = NULL;
        }
    }
    else { /* node nie jest pierwszy */
        /* nie jest pierwszy ale jest ostatni */
        if (r->last == node) {
            r->last = node->prev;
            r->last->next = NULL;
        }
        else { /* nie jest ani pierwszy ani ostatni */
            assert(node->prev);
            assert(node->next);
            node->prev->next = node->next;
            node->next->prev = node->prev;
        }
    }
    MonoDestroy(&node->mono);
    free(node);
}

/**
 * Kopiuje ogon wielomianu od `p_iter` włącznie i dokleja go do `r`
 * @param[out] r : wskaźnik do wielomianu
 * @param[in] p_iter : wskaźnik na pierwszy element z ogona do przekopiowania
 */
static void CopyTail(PolyPtr r, const PolyNode *p_iter) {
    assert(r);
    assert(p_iter);
    PolyNodePtr r_iter;
    PolyNodePtr new_node;
    /* przypadek gdy wielomian r jest zerowy: kopiujemy do niego pierwszy
     * element (w tym m-cu zawsze istnieje co najmniej jeden) */
    if (PolyIsZero(r)) {
        new_node = (PolyNodePtr) malloc(sizeof(PolyNode));
        assert(new_node);
        new_node->mono = MonoClone(&(p_iter->mono));
        new_node->prev = NULL; /* to pierwszy element */
        new_node->next = NULL;
        r->first = new_node;
        r->last = new_node;
        p_iter = p_iter->next;
    }
    else {
        /* jesli nie jest zerowy ale jest staly to zamieniamy go na
         * jednomian staly, w takim przypadku wciaz mamy co najmniej jeden
         * wezel do skopiowania a wiec nie powstanie nielegalny wielomian
         * bedacy jednomianem stalym */
        if (PolyIsCoeff(r)) {
            new_node = (PolyNodePtr) malloc(sizeof(PolyNode));
            assert(new_node);
            new_node->mono = MonoFromCoeff(r->const_coeff, 0);
            new_node->prev = NULL; /* to pierwszy element */
            new_node->next = NULL;
            r->first = new_node;
            r->last = new_node;
        }
    }
    r_iter = r->last;

    while (p_iter != NULL) {
        new_node = (PolyNodePtr) malloc(sizeof(PolyNode));
        assert(new_node);
        new_node->mono = MonoClone(&(p_iter->mono));
        new_node->prev = r_iter;
        r_iter->next = new_node;
        r_iter = new_node;
        p_iter = p_iter->next;
    }
    r->last = r_iter;
    r->last->next = NULL;
}

/**
 * Dokleja węzeł na koniec wielomianu
 * @param[out] r : wskaźnik do wielomianu
 * @param[in] node : węzeł, który doklejamy na koniec
 */
static void AppendAsLast(PolyPtr r, PolyNodePtr node) {
    assert(r);
    assert(node);
    node->next = NULL;
    /* wielomian r jest pusty */
    if (r->first == NULL) {
        r->first = node;
        r->last = node;
        node->prev = NULL; /* node jest pierwszym wezłem */
    }
    else {
        /* wielomian r jest normalny */
        node->prev = r->last;
        node->prev->next = node;
        r->last = node;
    }
}

/**
 * Dokleja węzeł na początek wielomianu
 * @param[out] r : wskaźnik do wielomianu
 * @param[in] node : węzeł, który doklejamy na początek
 */
static void AppendAsFirst(PolyPtr r, PolyNodePtr node) {
    assert(r);
    assert(node);
    node->prev = NULL;
    /* wielomian r jest pusty */
    if (r->first == NULL) {
        r->first = node;
        r->last = node;
        node->next = NULL; /* node jest ostatnim węzłem */
    }
    else {
        /* wielomian r jest normalny */
        node->next = r->first;
        node->next->prev = node;
        r->first = node;
    }
}

/**
 * Mnoży dwa jednomiany
 * @param[in] m1 : jednomian
 * @param[in] m2 : jednomian
 * @return Jednomian będący iloczynem jednomianów
 */
static Mono MonoMul(const Mono *m1, const Mono *m2) {
    assert(m1);
    assert(m2);

    /* przypadek bazowy - mnożenie jednomianów stałych */
    if (MonoIsCoeff(m1) && MonoIsCoeff(m2))
        return MonoFromCoeff(m1->p.const_coeff * m2->p.const_coeff, 0);

    Mono res = (Mono) {.exp = m1->exp + m2->exp, .p = PolyMul(&m1->p, &m2->p)};

    return res;
}

/**
 * Zwraca jednomian przeciwny tj. jednomian o współczynniku będącym wielomianem
 * przeciwnym
 * @param[in] m : jednomian
 * @return `-m`
 */
static Mono MonoNeg(const Mono *m) {
    assert(m);
    return (Mono) {.exp = m->exp, .p = PolyNeg(&m->p)};
}

/**
 * Sprawdza czy jednomiany są równe
 * @param[in] m1 : jednomian
 * @param[in] m2 : jednomian
 * @return Czy jednomiany równe?
 */
static bool MonoIsEq(const Mono *m1, Mono *m2) {
    assert(m1);
    assert(m2);
    if (m1->exp != m2->exp || !PolyIsEq(&m1->p, &m2->p))
        return false;

    return true;
}

/**
 * Podnosi wielomian do potęgi
 * @param p : jednomian
 * @param n : wykładnik potęgowania
 * @return
 */
Poly PolyPower(const Poly *p, poly_exp_t n) {

    Poly res = PolyFromCoeff(1);
    Poly q = PolyClone(p);
    Poly t;
    while (n > 0) {
        if (n & 1) {
            t = PolyMul(&res, &q);
            PolyDestroy(&res);
            res = t;
        }

        t = PolyMul(&q, &q);
        PolyDestroy(&q);
        q = t;
        n = n >> 1;
    }
    PolyDestroy(&q);

    return res;
}

Poly PolyCompose(const Poly *p, unsigned count, const Poly q[]) {
    assert(p);

    /* przypadki brzegowe: pusta tablica lub wielomian stały */
    if (count == 0) {
        Poly z = PolyAt(p, 0);
        return z;
    }

    if (PolyIsCoeff(p)) {
        return PolyClone(p);
    }

    Poly r, r1, r2, t, res = PolyFromCoeff(0);
    PolyNodePtr p_iter = p->first;
    while (p_iter != NULL) {
        r1 = PolyCompose(&p_iter->mono.p, count - 1, q + 1);
        r2 = PolyPower(q, p_iter->mono.exp);
        r = PolyMul(&r1, &r2);
        PolyDestroy(&r1);
        PolyDestroy(&r2);
        t = PolyAdd(&res, &r);
        PolyDestroy(&res);
        PolyDestroy(&r);
        res = t;
        p_iter = p_iter->next;
    }

    return res;
}