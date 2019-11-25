/** @file
   Interfejs parsera

   @author Michał Szafraniuk
   @copyright Uniwersytet Warszawski
   @date 2017-05-26
*/

#ifndef __PARSER_H__
#define __PARSER_H__

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "poly.h"
#include <unistd.h>
#include <limits.h>

/** Typ wyliczeniowy dla poleceń kalkulatora */
typedef enum {
    ZERO,
    IS_COEFF,
    IS_ZERO,
    CLONE,
    ADD,
    MUL,
    NEG,
    SUB,
    IS_EQ,
    DEG,
    DEG_BY,
    AT,
    PRINT,
    POP,
    COMPOSE
} Command;

/**
 * Funkcja wczytująca dowolnie długą linię.
 * @param f : plik do czytania
 * @return wskaźnik do początku bufora z wczytaną linią
 */
char *GetLine(FILE * f);

/**
 * Sprawdza czy napis jest kandydatem na polecenie kalkulatora.
 * @param[in] line : poczatek ciagu znaków
 * @return 1 gdy napis jest potencjalnie poleceniem, 0 w przeciwnym przypadku
 */
int LineIsPotentialCommand(char *line);

/**
 * Funkcja parsująca polecenie.
 * W przypadku powodzenia ustawia odpowiednie pola `cmd` oraz ew. `cmd_arg`.
 * W przypadku błędu ustawia `msg`.
 * @param[in] line : początek ciągu znaków
 * @param[out] cmd : sparsowane polecenie
 * @param[out] cmd_arg : sparsowany argument polecenia
 * @param[out] msg : komunikat diagnostyczny
 * @return 0 gdy sukces, 1 gdy błąd
 */
int ParseCommand(char *line, Command *cmd, poly_coeff_t *cmd_arg, char *msg);

/**
 * Funkcja parsująca wielomian. Bezpośrednio parsuje tylko jednomian stały,
 * w przypadku jednamianów normalnych parsowanie zleca funckji ParseMono.
 * Parsowane wielomiany trzyma w dynamicznie alokowanej tablicy, by pod koniec
 * wywołać PolyAddMonos.
 * @param[in,out] line
 * @param[in] leftDelimNo
 * @return
 */
Poly* ParsePoly(char **line, int *leftDelimNo);

/**
 * Funkcja sprawdzająca czy linia jest
 * @param[in] line : linia do sprawdzenia
 * @return true jeśli line jest NULLem lub wskazuje na napis o długości zero
 */
bool LineEmpty(char *line);

/**
 * Funkcja wypisująca na standardowe wyjście wielomian w formie wymaganej w
 * specyfikacji
 * @param[in] p : wielomian do wypisania
 */
void ParserPrintPoly(Poly *p);

/**
 * Funkcja wypisująca na standardowe wyjście jednomian w formie wymaganej w
 * specyfikacji
 * @param[in] m : jednomian do wypisania
 */
void ParserPrintMono(Mono m);

/**
 * Funkcja parsująca współczynnik będący stałą.
 * Sprawdza m.in. czy parsowana liczba nie wykracza poza LONG_MAX lub LONG_MIN.
 * @param[in,out] str : wskaźnik do wskaźnika będącego początkiem ciągu znaków do
 * parsowania
 * @param[out] res : sparsowany współczynnik będący stałą
 * @return zero w przypadku sukcesu, niezero w pzypadku błędu
 */
int ParseCoeff(char **str, poly_coeff_t *res);

/**
 * Funkcja parsująca wykładnik. Sprawdza m.in. czy parsowana liczba nie wykracza
 * poza INT_MAX.
 * @param[in] str : wskaźnik do wskaźnika będącego początkiem ciągu znaków do
 * parsowania
 * @param[out] res : sparsowany wykładnik
 * @return zero w przypadku sukcesu, niezero w pzypadku błędu
 */
int ParseExp(char **str, poly_exp_t *res);

#endif //__PARSER_H__
