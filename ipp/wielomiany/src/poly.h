/** @file
   Interfejs klasy wielomianów

   @author Michał Szafraniuk
   @copyright Uniwersytet Warszawski
   @date 2017-05-13
*/
#ifndef __POLY_H__
#define __POLY_H__

#include <stdbool.h>
#include <stddef.h>
#include <stdlib.h>
#include <assert.h>
#include <math.h>

/** Typ współczynników wielomianu */
typedef long poly_coeff_t;

/** Typ wykładników wielomianu */
typedef int poly_exp_t;

/** Typ struktury wielomianu */
typedef struct Poly Poly;

/** Typ wskażnika do struktury Poly */
typedef Poly *PolyPtr;

/** Typ struktury jednomianu */
typedef struct Mono Mono;

/** Typ wskaźnika do struktury Mono */
typedef struct Mono *MonoPtr;

/** Typ struktury węzła wielomianu */
typedef struct PolyNode PolyNode;

/** Typ wskaźnika do węzła wielomianu */
typedef PolyNode *PolyNodePtr;

/**
 * Struktura opakowująca wielomian.
 * Wyrózniamy dwa rodzaje wielomianów: wielomiany stałe oraz normalne, które
 * rozróżniamy w tej reprezentacji za pomocą zmiennej `first`.
 * Dla wielomianów stałych pole `first` jest ustawione na `NULL` i wówczas pole
 * `const_coeff` reprezentuje stałą. Wielomiany normalne przechowywane są jako
 * lista dwukierunkowa węzłów. Pole `first` jest wówczas różne od `NULL`a i
 * wskazuje na pierwszy węzeł listy a pole `last` na ostatni. Dla oszczędności
 * pamięci użyto unii.
 */
typedef struct Poly {
    PolyNodePtr first; ///< wskaźnik na pierwszy element wielomianu normalnego
    ///< lub `NULL` dla wielomianu stałego
    union {
        PolyNodePtr last; ///< wskaźnik na ostatni element wielomianu normalnego
        poly_coeff_t const_coeff; ///< stała reprezentująca wielomian stały
    };
} Poly;

/**
  * Struktura przechowująca jednomian
  * Jednomian ma postać `p * x^e`.
  * Współczynnik `p` może też być wielomianem.
  * Będzie on traktowany jako wielomian nad kolejną zmienną (nie nad x).
  */
typedef struct Mono {
    Poly p; ///< współczynnik
    poly_exp_t exp; ///< wykładnik
} Mono;

/**
 * Struktura przechowujaca węzeł wielomianu, który składa się z jednomianu
 */
typedef struct PolyNode {
    Mono mono; ///< jednomian
    PolyNodePtr prev; ///< wskaźnik na poprzedni węzeł
    PolyNodePtr next; ///< wskaźnik na następny węzeł
} PolyNode;

/**
 * Tworzy wielomian, który jest współczynnikiem.
 * @param[in] c : wartość współczynnika
 * @return wielomian
 */
static inline Poly PolyFromCoeff(poly_coeff_t c) {
    return (Poly) {.first = NULL, .const_coeff = c};
}

/**
 * Tworzy jednomian, którego współczynnikiem jest wielomian stały
 * @param[in] c : wartość współczynnika
 * @param[in] n : wartość wykładnika
 * @return jednomian o stałym wspólczynniku
 */
static inline Mono MonoFromCoeff(poly_coeff_t c, poly_exp_t n) {
    return (Mono) {.p = PolyFromCoeff(c), .exp = n};
}

/**
 * Tworzy wielomian tożsamościowo równy zeru.
 * @return wielomian
 */
static inline Poly PolyZero() {
    return PolyFromCoeff(0);
}

/**
 * Tworzy jednomian `p * x^e`.
 * Tworzony jednomian przejmuje na własność (kopiuje) wielomian @p p.
 * @param[in] p : wielomian - współczynnik jednomianu
 * @param[in] e : wykładnik
 * @return jednomian `p * x^e`
 */
static inline Mono MonoFromPoly(Poly *p, poly_exp_t e) {
    return (Mono) {.p = *p, .exp = e};
}

/**
 * Sprawdza, czy wielomian jest współczynnikiem.
 * @param[in] p : wielomian
 * @return Czy wielomian jest współczynnikiem?
 */
static inline bool PolyIsCoeff(const Poly *p) {
    return (p->first == NULL);
}

/**
 * Sprawdza, czy jednomian jest jednomianem stałym i.e. czy ma wykładnik zero
 * i wpsółczynnik będący wielomianem stałym.
 * @param[in] m : jednomian
 * @return Czy jednomian jest stały?
 */
static inline bool MonoIsCoeff(const Mono *m) {
    return (m->exp == 0 && PolyIsCoeff(&(m->p)));
}

/**
 * Sprawdza, czy wielomian jest tożsamościowo równy zeru.
 * @param[in] p : wielomian
 * @return Czy wielomian jest równy zero?
 */
static inline bool PolyIsZero(const Poly *p) {
    return (p->first == NULL && p->const_coeff == 0);
}

/**
 * Sprawdza, czy jednomian jest zerowy tzn. czy wielomian będący jego
 * współczynnikiem jest zerowy
 * @param[in] m : jednomian
 * @return Czy jednomian jest zerowy?
 */
static inline bool MonoIsZero(const Mono *m) {
    return (PolyIsZero(&m->p));
}

/**
 * Usuwa wielomian z pamięci.
 * @param[in] p : wielomian
 */
void PolyDestroy(Poly *p);

/**
 * Usuwa jednomian z pamięci.
 * @param[in] m : jednomian
 */
static inline void MonoDestroy(Mono *m) {
    assert(m);
    PolyDestroy(&m->p);
}

/**
 * Robi pełną kopię wielomianu.
 * @param[in] p : wielomian
 * @return skopiowany wielomian
 */
Poly PolyClone(const Poly *p);

/**
 * Robi pełną kopię jednomianu.
 * @param[in] m : jednomian
 * @return skopiowany jednomian
 */
static inline Mono MonoClone(const Mono *m) {
    return (Mono) {.p = PolyClone(&m->p), .exp = m->exp};
}

/**
 * Dodaje dwa wielomiany.
 * @param[in] p : wielomian
 * @param[in] q : wielomian
 * @return `p + q`
 */
Poly PolyAdd(const Poly *p, const Poly *q);

/**
 * Sumuje listę jednomianów i tworzy z nich wielomian.
 * @param[in] count : liczba jednomianów
 * @param[in] monos : tablica jednomianów
 * @return wielomian będący sumą jednomianów
 */
Poly PolyAddMonos(unsigned count, const Mono monos[]);

/**
 * Mnoży dwa wielomiany.
 * @param[in] p : wielomian
 * @param[in] q : wielomian
 * @return `p * q`
 */
Poly PolyMul(const Poly *p, const Poly *q);

/**
 * Zwraca przeciwny wielomian.
 * @param[in] p : wielomian
 * @return `-p`
 */
Poly PolyNeg(const Poly *p);

/**
 * Odejmuje wielomian od wielomianu.
 * @param[in] p : wielomian
 * @param[in] q : wielomian
 * @return `p - q`
 */
Poly PolySub(const Poly *p, const Poly *q);

/**
 * Zwraca stopień wielomianu ze względu na zadaną zmienną (-1 dla wielomianu
 * tożsamościowo równego zeru).
 * Zmienne indeksowane są od 0.
 * Zmienna o indeksie 0 oznacza zmienną główną tego wielomianu.
 * Większe indeksy oznaczają zmienne wielomianów znajdujących się
 * we współczynnikach.
 * @param[in] p : wielomian
 * @param[in] var_idx : indeks zmiennej
 * @return stopień wielomianu @p p z względu na zmienną o indeksie @p var_idx
 */
poly_exp_t PolyDegBy(const Poly *p, unsigned var_idx);

/**
 * Zwraca stopień wielomianu (-1 dla wielomianu tożsamościowo równego zeru).
 * @param[in] p : wielomian
 * @return stopień wielomianu @p p
 */
poly_exp_t PolyDeg(const Poly *p);

/**
 * Sprawdza równość dwóch wielomianów.
 * @param[in] p : wielomian
 * @param[in] q : wielomian
 * @return `p = q`
 */
bool PolyIsEq(const Poly *p, const Poly *q);

/**
 * Wylicza wartość wielomianu w punkcie @p x.
 * Wstawia pod pierwszą zmienną wielomianu wartość @p x.
 * W wyniku może powstać wielomian, jeśli współczynniki są wielomianem
 * i zmniejszane są indeksy zmiennych w takim wielomianie o jeden.
 * Formalnie dla wielomianu @f$p(x_0, x_1, x_2, \ldots)@f$ wynikiem jest
 * wielomian @f$p(x, x_0, x_1, \ldots)@f$.
 * @param[in] p
 * @param[in] x
 * @return @f$p(x, x_0, x_1, \ldots)@f$
 */
Poly PolyAt(const Poly *p, poly_coeff_t x);

/**
 * Wykonuje podstawienie tablicy pod odpowiednie zmienne w wielomianie
 * @param p wejściowy wielomian pod którego zmienne podstawiamy
 * @param count : ilość zmiennych do podstawienia
 * @param q : tablica z wielomianami, które podstawiamy w oryginalne zmienne
 * @return wielomian wynikowy powstały z p przez podstawienie
 */
Poly PolyCompose(const Poly *p, unsigned count, const Poly q[]);


#endif /* __POLY_H__ */
