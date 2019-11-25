/** @file
   Implementacja kalkulatora stosowego

   @author Michał Szafraniuk
   @copyright Uniwersytet Warszawski
   @date 2017-05-26
*/

#include "stack.h"
#include "utils.h"

/** Ograniczenie na długość komunikatu diagnostycznego */
#define MAX_MSG_LENGTH 32

/**
 * Główna funkcja kalkulatora
 * @return 0 w przypadku powodzenia
 */

int main() {

    //todo usunac

    printf("printf INSIDE CALC\n");
    fprintf(stderr,"frpintf INSIDE CALC\n");
    char *s;
    s = GetLine(stdin);
    if (LineEmpty(s))
        printf("Empty");
    else
        printf("Nonempty");
    printf("_%s_\n", s);




    PolyStackPtr stack = InitStack();

    char *line;
    char msg[MAX_MSG_LENGTH];
    StackErrors *stackErrorMsg = malloc(sizeof(StackErrors));
    Command *cmd = malloc(sizeof(Command));
    poly_coeff_t *cmd_arg = malloc(sizeof(poly_coeff_t));
    int ret_code;
    Poly *poly;
    unsigned long row = 1;
    int delims = 0;
    char *startLine;

    printf("PRZED");

    int temp = 0;


    while (!LineEmpty((line = GetLine(stdin)))) {
        temp++;
        printf("WHILE: $%s#", line);

        /*
        startLine = line;
        printf("%s#", line);
        // W pierwszej kolejności próbuj sparsować polecenie.
        if (LineIsPotentialCommand(line)) {
            ret_code = ParseCommand(line, cmd, cmd_arg, msg);
            if (ret_code != 0)
                fprintf(stderr, "ERROR %ld %s\n", row, msg);
            else {
                ret_code = run_command(stack, cmd, cmd_arg, stackErrorMsg);
                if (ret_code != 0) {
                    if (*stackErrorMsg == STACK_UNDERFLOW)
                    fprintf(stderr, "ERROR %ld STACK UNDERFLOW\n", row);
                }
            }
        }
        else {
            // W drugiej kolejności próbujemy sprasować wielomian
            poly = ParsePoly(&line, &delims); //todo alokacja poly?
            if (poly == NULL)
                fprintf(stderr, "ERROR %ld %ld\n", row, line - startLine + 1);
            else {
                Push(stack, poly);
            }
        }
        row++;
        free(startLine);

        */

    }

    printf("AFTER temp =%d", temp);


    /* Zwolnij pamięć zaalokowaną w funkcji main() kalkulatora */
    StackDestroy(stack);
    free(line);
    free(cmd);
    free(cmd_arg);
    free(stackErrorMsg);

    return 0;
}

