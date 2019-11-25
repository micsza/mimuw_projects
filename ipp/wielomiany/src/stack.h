/** @file
   Interfejs stosu wielomianów

   @author Michał Szafraniuk
   @copyright Uniwersytet Warszawski
   @date 2017-05-26
*/
#ifndef __STACK_H__
#define __STACK_H__

#include "parser.h"

/**
 * Typ wyliczeniowy dla błędów stosu.
 */
typedef enum {
    STACK_UNDERFLOW
} StackErrors;

/**
 * Pomocnicza nazwa dla typu stos
 */
typedef struct PolyStack PolyStack;

/**
 * Pomocnicza nazwa dla typu węzeł stosu
 */
typedef struct PolyStackNode PolyStackNode;

/**
 * Pomocnicza nazwa dla typu wskaźnik do węzła stosu
 */
typedef PolyStackNode *PolyStackNodePtr;

/**
 * Pomocnicza nazwa dla typu wskaźnik do stosu
 */
typedef PolyStack * PolyStackPtr;

/**
 * Struktura węzła stosu.
 */
struct PolyStackNode {
    PolyPtr poly; ///< wskaźnik do wielomianu
    PolyStackNodePtr next; ///< wskaźnik na następny element stosu
};

/**
 * Struktura opakowująca stos.
 */
struct PolyStack {
    PolyStackNodePtr top; ///< wskaźnik na wierzch stosu
    long size; ///< rozmiar stosu
};

/**
 * Funkcja inicjująca stos.
 * @return wskaźnik do nowo powstałego stosu.
 */
PolyStackPtr InitStack();

/**
 * Funkcja dająca element z wierzchołka stosu.
 * @param[in] s : stos
 * @return wskaźnik na element z wierzchołka stosu
 */
PolyPtr Top(PolyStackPtr s);

/**
 * Funkcja dająca drugi element z wierzchołka stosu
 * @param[in] s : stos
 * @return wskaźnik na element bezpośrednio pod wierzchołkiem stosu
 */
PolyPtr Second(PolyStackPtr s);

/**
 * Funkcja odkładająca wielomian na stos
 * @param[in] s : stos
 * @param[in] p : wielomian
 */
void Push(PolyStackPtr s, Poly *p);

/**
 * Funkcja zdejmująca element z wierzchołka stosu.
 * @param[in] s : stos
 * @return wskaźnik do ściągniętego z czubka wielomianu
 */
PolyPtr Pop(PolyStackPtr s);

/**
 * Funkcja zwracająca rozmiar wielomianu.
 * @param[in] s : stos
 * @return rozmiar wielomianu
 */
long Size(PolyStackPtr s);

/**
 * Funkcja sprawdzająca czy stos jest pusty
 * @param[in] s : stos
 * @return czy stos pusty
 */
bool IsEmpty(PolyStackPtr s);

/**
 * Funkcja zarządzająca wykonaniem komendy na stosie.
 * @param[in] stack : stos
 * @param[in] cmd : komenda
 * @param[in] arg : argument komendy
 * @param[out] errmsg : komunikat diagnostyczny ustawiany w przypadku błędu
 * @return 0 w przypadku sukcesu, 1 w przypadku błędu
 */
int run_command(PolyStackPtr stack, Command *cmd, poly_coeff_t *arg,
                StackErrors* errmsg);

/**
 * Funkcja usuwająca stos z pamięci.
 * @param[in] s : stos
 */
void StackDestroy(PolyStackPtr s);

#endif //__STACK_H__
