/** @file
   Implementacja stosu wielomianów

   @author Michał Szafraniuk
   @copyright Uniwersytet Warszawski
   @date 2017-05-26
*/

#include "stack.h"

PolyStackPtr InitStack() {
    PolyStackPtr s = (PolyStackPtr) malloc(sizeof(PolyStack));
    s->top = NULL;
    s->size = 0;
    assert(s);

    return s;
}

PolyPtr Top(PolyStackPtr s) {
    return s->top->poly;
}

PolyPtr Second(PolyStackPtr s) {
    return s->top->next->poly;
}

void Push(PolyStackPtr s, Poly *p) {
    assert(p);
    PolyStackNodePtr new_node = (PolyStackNodePtr) malloc(sizeof(PolyStackNode));
    assert(new_node);
    new_node->poly = p;
    new_node->next = s->top;
    s->top = new_node;
    s->size++;
}

PolyPtr Pop(PolyStackPtr s) {
    assert(s->size > 0);
    PolyPtr ret = s->top->poly;
    PolyStackNodePtr tmp = s->top;
    s->top = s->top->next;
    s->size--;
    free(tmp);

    return ret;
}

long Size(PolyStackPtr s) {
    return s->size;
}

bool IsEmpty(PolyStackPtr s) {
    return (s->size == 0);
}

int run_command(PolyStackPtr stack, Command *cmd, poly_coeff_t *arg, StackErrors *errmsg) {
    assert(stack);

    if (*cmd == ZERO) {
        PolyPtr p = malloc(sizeof(Poly));
        assert(p);
        *p = PolyZero();
        Push(stack, p);
        return 0;
    }

    if (*cmd == IS_COEFF) {
        if (Size(stack) < 1) {
            *errmsg = STACK_UNDERFLOW;
            return 1;
        }
        if (PolyIsCoeff(Top(stack)) == true)
            printf("1\n");
        else
            printf("0\n");
        return 0;
    }

    if (*cmd == IS_ZERO) {
        if (Size(stack) < 1) {
            *errmsg = STACK_UNDERFLOW;
            return 1;
        }
        if (PolyIsZero(Top(stack)) == true)
            printf("1\n");
        else
            printf("0\n");
        return 0;
    }

    if (*cmd == CLONE) {
        if (Size(stack) < 1) {
            *errmsg = STACK_UNDERFLOW;
            return 1;
        }
        PolyPtr p = malloc(sizeof(Poly));
        assert(p);
        *p = PolyClone(Top(stack));
        Push(stack, p);
        return 0;
    }

    if (*cmd == ADD) {
        if (Size(stack) < 2) {
            *errmsg = STACK_UNDERFLOW;
            return 1;
        }
        Poly *p = Pop(stack);
        Poly *q = Pop(stack);
        Poly *r = malloc(sizeof(Poly));
        assert(r);
        *r = PolyAdd(p, q);
        Push(stack, r);
        PolyDestroy(p);
        PolyDestroy(q);
        free(p);
        free(q);
        return 0;
    }

    if (*cmd == MUL) {
        if (Size(stack) < 2) {
            *errmsg = STACK_UNDERFLOW;
            return 1;
        }
        Poly *p = Pop(stack);
        Poly *q = Pop(stack);
        Poly *r = malloc(sizeof(Poly));
        assert(r);
        *r = PolyMul(p, q);
        Push(stack, r);
        PolyDestroy(p);
        PolyDestroy(q);
        free(p);
        free(q);

        return 0;
    }

    if (*cmd == NEG) {
        if (Size(stack) < 1) {
            *errmsg = STACK_UNDERFLOW;
            return 1;
        }
        Poly *p = Pop(stack);
        Poly *r = malloc(sizeof(Poly));
        assert(r);
        *r = PolyNeg(p);
        Push(stack, r);
        PolyDestroy(p);
        free(p);
        return 0;
    }

    if (*cmd == SUB) {
        if (Size(stack) < 2) {
            *errmsg = STACK_UNDERFLOW;
            return 1;
        }
        Poly *p = Pop(stack);
        Poly *q = Pop(stack);
        Poly *r = malloc(sizeof(Poly));
        assert(r);
        *r = PolySub(p, q);
        Push(stack, r);
        PolyDestroy(p);
        PolyDestroy(q);
        free(p);
        free(q);
        return 0;
    }

    if (*cmd == IS_EQ) {
        if (Size(stack) < 2) {
            *errmsg = STACK_UNDERFLOW;
            return 1;
        }
        if (PolyIsEq(Top(stack), Second(stack)) == true)
            printf("1\n");
        else
            printf("0\n");
        return 0;
    }

    if (*cmd == DEG) {
        if (Size(stack) < 1) {
            *errmsg = STACK_UNDERFLOW;
            return 1;
        }
        printf("%d\n", PolyDeg(Top(stack)));
        return 0;
    }

    if (*cmd == DEG_BY) {
        if (Size(stack) < 1) {
            *errmsg = STACK_UNDERFLOW;
            return 1;
        }
        printf("%d\n", PolyDegBy(Top(stack), (unsigned) *arg));
        return 0;
    }

    if (*cmd == AT) {
        if (Size(stack) < 1) {
            *errmsg = STACK_UNDERFLOW;
            return 1;
        }
        Poly *p = Pop(stack);
        Poly *r = malloc(sizeof(Poly));
        assert(r);
        *r = PolyAt(p, *arg);
        Push(stack, r);
        PolyDestroy(p);
        free(p);
        return 0;
    }

    // TODO
    if (*cmd == COMPOSE) {
        // todo co jesli count = 0? eśli count jest równe zeru, to funkcja zwraca po prostu wielomian stały będący wartością wielomianu p w „zerze”.
        unsigned count = (unsigned) *arg;
        //printf("compose count = %d\n", count);
        if (Size(stack) < count + 1) {
            *errmsg = STACK_UNDERFLOW;
            return 1;
        }

        Poly *p = Pop(stack); //todo bez mallocka?
        Poly *t;
        Poly q[count];
        int i = 0;
        for (i = 0; i < count; i++) {
            t = Pop(stack);
            q[i] = *t;
            free(t);
            //PrintPoly(&q[i]);
        }
        Poly *r = malloc(sizeof(Poly));
        assert(r);
        *r = PolyCompose(p, count, q);
        Push(stack, r);
        PolyDestroy(p);
        free(p);
        // todo zwolnic tablice q[]
        for (i = 0; i < count; i++) {
            PolyDestroy(&q[i]);
            //free(q[i]);
        }
        return 0;
    }

    if (*cmd == PRINT) {
        if (Size(stack) < 1) {
            *errmsg = STACK_UNDERFLOW;
            return 1;
        }
        ParserPrintPoly(Top(stack));
        printf("\n");
        return 0;
    }

    if (*cmd == POP) {
        if (Size(stack) < 1) {
            *errmsg = STACK_UNDERFLOW;
            return 1;
        }
        Poly *p = Pop(stack);
        PolyDestroy(p);
        free(p);
        return 0;
    }

    return 1;
}

void StackDestroy(PolyStackPtr s) {
    assert(s);
    PolyStackNodePtr iter = s->top, tmp;
    while (iter != NULL) {
        PolyDestroy(iter->poly);
        free(iter->poly);
        tmp = iter;
        iter = iter->next;
        free(tmp);
    }
    free(s);
}