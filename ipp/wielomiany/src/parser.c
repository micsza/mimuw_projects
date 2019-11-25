/** @file
   Implementacja parsera

   @author Michał Szafraniuk
   @copyright Uniwersytet Warszawski
   @date 2017-05-26
*/

#include "parser.h"

/** Ograniczenie na długość komunikatu polecenia */
#define MAX_COMMAND_LENGTH 30

/** Podstawa arytmetyki */
#define BASE 10

/** Początkowy rozmiar tablicy jednomianów w funkcji ParsePoly */
#define INITIAL_MONOS_ARR_SIZE 512

/** Makro komunikatu diagnostycznego */
#define WRONG_COMMAND_MSG "WRONG COMMAND"

/** Makro komunikatu diagnostycznego */
#define WRONG_VALUE_MSG "WRONG VALUE"

/** Makro komunikatu diagnostycznego */
#define WRONG_VARIABLE_MSG "WRONG VARIABLE"

/** Makro komunikatu diagnostycznego */
#define WRONG_COUNT_MSG "WRONG COUNT"

/** Makro polecenia */
#define ZERO_CMD "ZERO"

/** Makro polecenia */
#define IS_COEFF_CMD "IS_COEFF"

/** Makro polecenia */
#define IS_ZERO_CMD "IS_ZERO"

/** Makro polecenia */
#define CLONE_CMD "CLONE"

/** Makro polecenia */
#define ADD_CMD "ADD"

/** Makro polecenia */
#define MUL_CMD "MUL"

/** Makro polecenia */
#define NEG_CMD "NEG"

/** Makro polecenia */
#define SUB_CMD "SUB"

/** Makro polecenia */
#define IS_EQ_CMD "IS_EQ"

/** Makro polecenia */
#define DEG_CMD "DEG"

/** Makro polecenia */
#define PRINT_CMD "PRINT"

/** Makro polecenia */
#define POP_CMD "POP"

/** Makro polecenia */
#define AT_CMD "AT"

/** Makro polecenia */
#define DEG_BY_CMD "DEG_BY"

/** Makro polecenia */
#define COMPOSE_CMD "COMPOSE"

char* GetLine(FILE * f) {
    size_t size = 0;
    size_t len  = 0;
    size_t last = 0;
    char *buf = NULL;
    bool line_end = false;

    while (!feof(f) && !line_end) {
        size += BUFSIZ;
        buf = realloc(buf, size);
        assert(buf);
        if (fgets(buf + last, (int) size - (int) last, f) == NULL)
            return NULL;
        len = strlen(buf);
        last = len - 1;
        if (last >= 0 && buf[last++] == '\n')
            line_end = true;
    }
    return buf;
}

int LineIsPotentialCommand(char *line) {
    if (isalpha(line[0]) && !isdigit(line[0]))
        return 1;
    else
        return 0;
}

bool LineEmpty(char *line) {
    printf("LineEmpty: line = %s, len =%d\n", line, strlen(line));
    if (line == NULL)
        return true;
    return strlen(line) == 0;
}

/**
 * Funkcja parsująca jednomian normalny postaci \f$(p,e)\f$, gdzie \f$ p \f$
 * jest wielomianem a \f$ e \f$ wykładnikiem.
 * @param[in,out] pline
 * @param[in] leftDelimNo
 * @return
 */
static Mono* ParseMono(char **pline, int *leftDelimNo) {
    poly_exp_t exp;
    Mono *m;
    Poly *p;
    assert(*leftDelimNo >= 0);

    /* Jeśli pierwszy znak nie jest lewym nawiasem to błąd. */
    if (**pline != '(')
        return NULL;
    (*pline)++;
    (*leftDelimNo)++;
    /* Sparsuj wielomian. */
    if ((p = ParsePoly(pline, leftDelimNo)) == NULL) {
        return NULL;
    }
    /* Wczytaj przecinek. */
    if (**pline != ',') {
        PolyDestroy(p);
        free(p);
        return NULL;
    }
    (*pline)++;
    /* Sparsuj wykładnik. */
    if (ParseExp(pline, &exp) != 0) {
        PolyDestroy(p);
        free(p);
        return NULL;
    }
    /* Wczytaj prawy nawias. */
    if (**pline != ')') {
        PolyDestroy(p);
        free(p);
        return NULL;
    }
    (*pline)++;
    (*leftDelimNo)--;

    /* Zbuduj jednomian. */
    m = malloc(sizeof(Mono));
    assert(m);
    m->p = *p;
    m->exp = exp;
    free(p);

    return m;
}

/**
 * Funkcja zwalniająca pamięć zaalokowaną jednomianom oraz tablicy, używana
 * w przypadku wykrycia błędu
 * @param[in] n : liczba jednomianów w tablicy
 * @param[in] monos : tablica jednomianów
 */
static void FreeMonosArray(unsigned n, Mono monos[]) {
    unsigned i;
    for (i = 0; i < n; i++) {
        MonoDestroy(&monos[i]);
    }
    free(monos);
}

Poly* ParsePoly(char **line, int *delim) {
    Poly *p;
    Mono *m;
    int leftDelimNo = 0;
    char **pline = line;
    Mono *monoPtr;
    unsigned int N = INITIAL_MONOS_ARR_SIZE; /* Aktualny rozmiar tablicy */
    unsigned int n = 0; /* Liczba poprawnie sparsowanych wielomianów */
    Mono *monos = NULL;
    void *tmp;
    poly_coeff_t coeff;

    monos = realloc(monos, (N * sizeof(Mono)));
    assert(monos);

    /* Przed pierwszym obrotem może być tylko lewy nawias lub liczba. */
    if ((**pline != '(') && (**pline != '-') && (!isdigit(**pline))) {
        FreeMonosArray(n, monos);
        return NULL;
    }

    /* W pętli parsujemy sumę jednomianów z wywołaniami rekurencyjnymi. */
    while(strlen(*pline) > 0 && (**pline != '\n')) {
        /* Jeśli sparsowaliśmy dotychczas co najmniej jeden jednomian,
         * to jedyny poprawny znak do wczytania to plus lub przecinek. */
        if (n > 0) {
            /* Jeśli przecinek, to jeśli liczba otwartych nawiasów dodatnia to
             * koniec parsowania wielomianu, wyjście z petli, stworzenie
             * wielomianu i powrót do funkcji parsującej jednomian,
             * w przeciwnym przypadku błąd. */
            if (**pline == ',') {
                if (*delim > 0)
                    break;
                else {
                    FreeMonosArray(n, monos);
                    return NULL;
                }
            }
            /* Jeśli plus, to oczekujemy co najmniej jednego kolejnego
             * jednomianu, w przeciwnym przypadku błąd. */
            if (**pline == '+') {
                (*pline)++;
                if (**pline != '(' && **pline != '-' && !isdigit(**pline)) {
                    FreeMonosArray(n, monos);
                    return NULL;
                }
            }
            else {
                /* Jeśi ani plus ani przecinek, to błąd. */
                FreeMonosArray(n, monos);
                return NULL;
            }
        }
        /* Jeśli piewszy znak to cyfra lub minus to sparsuj jednomian stały. */
        if (**pline == '-' || isdigit(**pline)) {
            if (ParseCoeff(pline, &coeff) != 0) {
                FreeMonosArray(n, monos);
                return NULL;
            }
            m = malloc(sizeof(Mono));
            assert(m);
            *m = MonoFromCoeff(coeff, 0);
            /* Sprawdź czy nie trzeba powiększyć rozmiaru tablicy. */
            if (n == N) {
                N *= 2;
                tmp = realloc(monos, (N * sizeof(Mono)));
                assert(tmp);
                monos = (Mono*) tmp;
            }
            monos[n++] = *m;
            free(m);
            continue;
        }

        /* Jeśli coś innego niż nawias otwierający to błąd. Nie przechodz po
         * nawiasach, gdyż tym zajmuje sie ParseMono. */
        if (**pline != '(') {
            FreeMonosArray(n, monos);
            return NULL;
        }

        /* Jeżeli nawias otwierający to arsuj jednomian normalny. */
        monoPtr = ParseMono(pline, &leftDelimNo);
        if (monoPtr == NULL) {
            FreeMonosArray(n, monos);
            return NULL;
        }

        else {
            /* Umieść jednomian w tablicy */
            if (n == N) {
                N *= 2;
                tmp = realloc(monos, (N * sizeof(Mono)));
                assert(tmp);
                monos = (Mono*) tmp;
            }
            monos[n++] = *monoPtr;
            free(monoPtr);
        }
    }

    /* Po wyjściu z pętli mamy albo pustą albo poprawną tablicę jednomianów. */
    if (n == 0) {
        free(monos);
        return NULL;
    }
    p = malloc(sizeof(Poly));
    assert(p);
    *p = PolyAddMonos(n, monos);

    free(monos);

    return p;
}

/**
 * Funkcja sprawdzająca czy napotkano znak końca linii lub końca napisu
 * @param[in] line : napis
 * @return 0 jeśli koniec, 1 w przeciwnym przypadku
 */
static int CheckIfEndOfLine(char *line) {
    if (*line == '\n' || *line == '\0') {
        return 0;
    }
    return 1;
}

/**
 * Funkcja parsująca argument dla polecenia `deg at`. Sprawdzane jest m.in.
 * przekroczenie wartości UINT_MAX
 * @param[in,out] str : wskaźnik do wskaźnika do ciągu znaków
 * @param[out] res : sparsowany argument w przypadku powodzenia
 * @return 0 gdy sukces, 1 w przeciwnym przypadku
 */
static int ParseDegArg(char **str, poly_exp_t *res) {
    poly_exp_t val = 0;
    int digit;
    if (!isdigit(**str)) {
        return 1;
    }
    /* Akceptujemy tylko cyfry. Sprawdzamy przekroczenie vs UINT_MAX */
    while (isdigit(**str)) {
        digit = **str - '0';

        if (val > UINT_MAX / BASE || (val == UINT_MAX / BASE && digit > UINT_MAX % BASE)) {
            return 1;
        }
        else {
            val = val * BASE + digit;
        }
        (*str)++;
    }

    *res = val;
    return 0;
}

int ParseCommand(char *line, Command *cmd, poly_coeff_t *cmd_arg, char *msg) {
    assert(cmd);

    char res[MAX_COMMAND_LENGTH];
    /* Na wejściu wiemy, że pierwszy znak to litera. */
    if (sscanf(line, "%s", res) == 1) {
        if (strcmp(res, ZERO_CMD) == 0) {
            line += strlen(ZERO_CMD);
            if (CheckIfEndOfLine(line) == 0) {
                *cmd = ZERO;
                return 0;
            }
            strcpy(msg, WRONG_COMMAND_MSG);
            return 1;
        }
        if (strcmp(res, IS_COEFF_CMD) == 0) {
            line += strlen(IS_COEFF_CMD);
            if (CheckIfEndOfLine(line) == 0) {
                *cmd = IS_COEFF;
                return 0;
            }
            strcpy(msg, WRONG_COMMAND_MSG);
            return 1;
        }
        if (strcmp(res, IS_ZERO_CMD) == 0) {
            line += strlen(IS_ZERO_CMD);
            if (CheckIfEndOfLine(line) == 0) {
                *cmd = IS_ZERO;
                return 0;
            }
            strcpy(msg, WRONG_COMMAND_MSG);
            return 1;
        }
        if (strcmp(res, CLONE_CMD) == 0) {
            line += strlen(CLONE_CMD);
            if (CheckIfEndOfLine(line) == 0) {
                *cmd = CLONE;
                return 0;
            }
            strcpy(msg, WRONG_COMMAND_MSG);
            return 1;
        }
        if (strcmp(res, ADD_CMD) == 0) {
            line += strlen(ADD_CMD);
            if (CheckIfEndOfLine(line) == 0) {
                *cmd = ADD;
                return 0;
            }
            strcpy(msg, WRONG_COMMAND_MSG);
            return 1;
        }
        if (strcmp(res, MUL_CMD) == 0) {
            line += strlen(MUL_CMD);
            if (CheckIfEndOfLine(line) == 0) {
                *cmd = MUL;
                return 0;
            }
            strcpy(msg, WRONG_COMMAND_MSG);
            return 1;
        }
        if (strcmp(res, NEG_CMD) == 0) {
            line += strlen(NEG_CMD);
            if (CheckIfEndOfLine(line) == 0) {
                *cmd = NEG;
                return 0;
            }
            strcpy(msg, WRONG_COMMAND_MSG);
            return 1;
        }
        if (strcmp(res, SUB_CMD) == 0) {
            line += strlen(SUB_CMD);
            if (CheckIfEndOfLine(line) == 0) {
                *cmd = SUB;
                return 0;
            }
            strcpy(msg, WRONG_COMMAND_MSG);
            return 1;
        }
        if (strcmp(res, IS_EQ_CMD) == 0) {
            line += strlen(IS_EQ_CMD);
            if (CheckIfEndOfLine(line) == 0) {
                *cmd = IS_EQ;
                return 0;
            }
            strcpy(msg, WRONG_COMMAND_MSG);
            return 1;
        }
        if (strcmp(res, DEG_CMD) == 0) {
            line += strlen(DEG_CMD);
            if (CheckIfEndOfLine(line) == 0) {
                *cmd = DEG;
                return 0;
            }
            strcpy(msg, WRONG_COMMAND_MSG);
            return 1;
        }
        if (strcmp(res, PRINT_CMD) == 0) {
            //printf("print, line =<%s> \n", line);
            line += strlen(PRINT_CMD);
            if (CheckIfEndOfLine(line) == 0) {
                *cmd = PRINT;
                return 0;
            }
            //printf(" .. err\n");
            strcpy(msg, WRONG_COMMAND_MSG);
            return 1;
        }
        if (strcmp(res, POP_CMD) == 0) {

            line += strlen(POP_CMD);
            if (CheckIfEndOfLine(line) == 0) {
                *cmd = POP;
                return 0;
            }
            strcpy(msg, WRONG_COMMAND_MSG);
            return 1;
        }
        if (strcmp(res, AT_CMD) == 0) {
            line += strlen(AT_CMD) + 1; /* Dodaj 1 by ominac znak '\0'. */
            if (ParseCoeff(&line, cmd_arg) == 0) {
                if (CheckIfEndOfLine(line) == 0) {
                    *cmd = AT;
                    return 0;
                }
            }
            strcpy(msg, WRONG_VALUE_MSG);
            return 1;
        }
        if (strcmp(res, DEG_BY_CMD) == 0) {
            line += strlen(DEG_BY_CMD) + 1; /* Dodaj 1 by ominac znak '\0'. */
            if (ParseDegArg(&line, (poly_exp_t*) cmd_arg) == 0) {
                if (CheckIfEndOfLine(line) == 0) {
                    *cmd = DEG_BY;
                    return 0;
                }
            }
            strcpy(msg, WRONG_VARIABLE_MSG);
            return 1;
        }
        if (strcmp(res, COMPOSE_CMD) == 0) {
            line += strlen(COMPOSE_CMD) + 1; /* Dodaj 1 by ominac znak '\0'. */
            if (ParseDegArg(&line, (poly_exp_t*) cmd_arg) == 0) {
                if (CheckIfEndOfLine(line) == 0) {
                    *cmd = COMPOSE;
                    return 0;
                }
            }
            strcpy(msg, WRONG_COUNT_MSG);
            return 1;
        }
    }
    strcpy(msg, WRONG_COMMAND_MSG);
    return 1;
}

/**
 * Pomocnicza funkcja do drukowania wielomianów
 * @param[in] pnode wskaźnik do węzła wielomianu
 */
static void ParserPrintPolyNode(PolyNodePtr pnode) {
    ParserPrintMono(pnode->mono);
}

void ParserPrintPoly(Poly *p) {
    assert(p);
    if (p->first == NULL)
        printf("%ld", p->const_coeff);
    else {
        PolyNodePtr ptr = p->first;
        while (ptr != NULL) {
            ParserPrintPolyNode(ptr);
            if (ptr->next != NULL)
                printf("+");
            ptr = ptr->next;
        }
    }
}

void ParserPrintMono(Mono m) {
    printf("(");
    ParserPrintPoly(&m.p);
    printf(",%d", m.exp);
    printf(")");
}

int ParseCoeff(char **str, poly_coeff_t *res) {
    bool isNegative = false;
    poly_coeff_t val = 0;
    int digit;
    if (**str != '-' && !isdigit(**str)) {
        return 1;
    }
    if (**str == '-') {
        isNegative = true;
        (*str)++;
    }
    if (!isdigit(**str)) {
        return 1;
    }
    /* Ze względu na asymetryczność zakresu musimy rozróżniać obydwa przypadki */
    while (isdigit(**str)) {
        digit = **str - '0';
        if (isNegative) {
            if (-1 * val < LONG_MIN / BASE || (-1 * val == LONG_MIN / BASE && digit > -1 * (LONG_MIN % BASE))) {
                return 1;
            }
        }
        else {
            if (val > LONG_MAX / BASE || (val == LONG_MAX / BASE && digit > LONG_MAX % BASE)) {
                return 1;
            }
        }
        val = val * BASE + digit;
        (*str)++;
    }

    if (isNegative) {
        val *= -1;
    }
    *res = val;
    return 0;
}

int ParseExp(char **str, poly_exp_t *res) {
    poly_exp_t val = 0;
    int digit;
    /* W tej funkcji nie akceptujemy minusa */
    if (!isdigit(**str)) {
        return 1;
    }
    while (isdigit(**str)) {
        digit = **str - '0';
        if (val > INT_MAX / BASE || (val == INT_MAX / BASE && digit > INT_MAX % BASE)) {
            return 1;
        }
        else {
            val = val * BASE + digit;
        }
        (*str)++;
    }
    *res = val;
    return 0;
}
