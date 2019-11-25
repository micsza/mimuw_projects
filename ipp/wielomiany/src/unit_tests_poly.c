#include <stdio.h>
#include <stdarg.h>
#include <setjmp.h>
#include <stdlib.h>
#include "cmocka.h"
#include "poly.h"
#include "parser.h"
#include "stack.h"

int mock_fprintf(FILE* const file, const char *format, ...) CMOCKA_PRINTF_ATTRIBUTE(2, 3);
int mock_printf(const char *format, ...) CMOCKA_PRINTF_ATTRIBUTE(1, 2);
extern int calc_poly_main();

/**
 * Pomocnicze bufory, do których piszą atrapy funkcji printf i fprintf oraz
 * pozycje zapisu w tych buforach. Pozycja zapisu wskazuje bajt o wartości 0.
 */
static char fprintf_buffer[256];
static char printf_buffer[256];
static int fprintf_position = 0;
static int printf_position = 0;

/************* MOCK FUNCTIONS ********************/

/**
 * Atrapa funkcji fprintf sprawdzająca poprawność wypisywania na stderr.
 */
int mock_fprintf(FILE* const file, const char *format, ...) {
    int return_value;
    va_list args;

    assert_true(file == stderr);
    /* Poniższa asercja sprawdza też, czy fprintf_position jest nieujemne.
    W buforze musi zmieścić się kończący bajt o wartości 0. */
    assert_true((size_t)fprintf_position < sizeof(fprintf_buffer));

    va_start(args, format);
    return_value = vsnprintf(fprintf_buffer + fprintf_position,
                             sizeof(fprintf_buffer) - fprintf_position,
                             format,
                             args);
    va_end(args);

    fprintf_position += return_value;
    assert_true((size_t)fprintf_position < sizeof(fprintf_buffer));
    return return_value;
}

/**
 * Atrapa funkcji printf sprawdzająca poprawność wypisywania na stderr.
 */
int mock_printf(const char *format, ...) {
    int return_value;
    va_list args;

    /* Poniższa asercja sprawdza też, czy printf_position jest nieujemne.
    W buforze musi zmieścić się kończący bajt o wartości 0. */
    assert_true((size_t)printf_position < sizeof(printf_buffer));

    va_start(args, format);
    return_value = vsnprintf(printf_buffer + printf_position,
                             sizeof(printf_buffer) - printf_position,
                             format,
                             args);
    va_end(args);

    printf_position += return_value;
    assert_true((size_t)printf_position < sizeof(printf_buffer));
    return return_value;
}

/**
 *  Pomocniczy bufor, z którego korzystają atrapy funkcji operujących na stdin.
 */
# define MAX_INPUT_BUFFER_SIZE 256
static char input_stream_buffer[MAX_INPUT_BUFFER_SIZE];
static int input_stream_position = 0;
static int input_stream_end = 0;
int read_char_count;

/**
 * Atrapa funkcji scanf używana do przechwycenia czytania z stdin.
 */
int mock_scanf(const char *format, ...) {
    va_list fmt_args;
    int ret;

    va_start(fmt_args, format);
    ret = vsscanf(input_stream_buffer + input_stream_position, format, fmt_args);
    va_end(fmt_args);

    if (ret < 0) { /* ret == EOF */
        input_stream_position = input_stream_end;
    }
    else {
        assert_true(read_char_count >= 0);
        input_stream_position += read_char_count;
        if (input_stream_position > input_stream_end) {
            input_stream_position = input_stream_end;
        }
    }
    return ret;
}

/**
 * Atrapa funkcji getchar używana do przechwycenia czytania z stdin.
 */
int mock_getchar() {
    if (input_stream_position < input_stream_end)
        return input_stream_buffer[input_stream_position++];
    else
        return EOF;
}

/**
 * Atrapa funkcji ungetc.
 * Obsługiwane jest tylko standardowe wejście.
 */
int mock_ungetc(int c, FILE *stream) {
    assert_true(stream == stdin);
    if (input_stream_position > 0)
        return input_stream_buffer[--input_stream_position] = c;
    else
        return EOF;
}

/**
 * Atrapa funkcja GetLine.
 * Obsługowane jest tylko standarodwe wejście.
 * @param stream
 * @return
 */
char * mock_GetLine(FILE *stream) {
    printf("MOCK_GetLine: input_stream_buffer = [%s], pos = %d\n", input_stream_buffer, input_stream_position);
    //assert_true(stream == stdin);
    int i = input_stream_position;

    while (i < MAX_INPUT_BUFFER_SIZE && input_stream_buffer[i] != '\n' && input_stream_buffer[i] != '\0')
        i++;

    input_stream_position = i;
    printf("MOCK_GetLine: i = %d\n", i);

    return input_stream_buffer+i;

}


/**
 * Funkcja inicjująca dane wejściowe dla programu korzystającego ze stdin.
 */
static void init_input_stream(const char *str) {
    memset(input_stream_buffer, 0, sizeof(input_stream_buffer));
    input_stream_position = 0;
    input_stream_end = strlen(str);
    assert_true((size_t)input_stream_end < sizeof(input_stream_buffer));
    strcpy(input_stream_buffer, str);
}


/************* TESTY ********************/



/* Funkcja testująca PolyCompose dla:
 * p = wielomian zerowy, count = 0. */
static void test_PolyCompose_zero_zero_null(void **state) {
    (void)state;

    Poly p = PolyZero();
    Poly res = PolyCompose(&p, 0, NULL);
    assert_true(PolyIsZero(&res));
}

/* Funkcja testująca PolyCompose dla:
 * p wielomian zerowy, count równe 1, x[0] wielomian stały. */
static void test_PolyCompose_zero_one_coeff(void **state) {
    (void)state;

    poly_coeff_t c = 1;
    Poly p = PolyZero();
    Poly x = PolyFromCoeff(c);
    Poly res = PolyCompose(&p, 1, &x);
    assert_true(PolyIsZero(&res));
}

/* Funkcja testująca PolyCompose dla:
 * p wielomian stały, count równe 0. */
static void test_PolyCompose_coeff_zero_null(void **state) {
    (void)state;

    poly_coeff_t c = 2;
    Poly p = PolyFromCoeff(c);
    Poly res = PolyCompose(&p, 0, NULL);
    assert_true(PolyIsEq(&res, &p));
}

/* Funkcja testująca PolyCompose dla:
 * p wielomian stały, count równe 1, x[0] wielomian stały różny od p. */
static void test_PolyCompose_coeff_one_coeff(void **state) {
    (void)state;

    poly_coeff_t c1 = 3;
    poly_coeff_t c2 = 4;
    Poly p = PolyFromCoeff(c1);
    Poly x = PolyFromCoeff(c2);
    Poly res = PolyCompose(&p, 1, &x);
    assert_true(PolyIsEq(&res, &p));
}

/* Funkcja testująca PolyCompose dla:
 * p wielomian x0, count równe 0. */
static void test_PolyCompose_x_zero_null(void **state) {
    (void)state;

    Mono m = MonoFromCoeff(1, 1);
    Poly p = PolyAddMonos(1, &m);
    Poly res = PolyCompose(&p, 0, NULL);
    Poly res2 = PolyAt(&p, 0);
    assert_true(PolyIsEq(&res, &res2));
    PolyDestroy(&p);
}

/* Funkcja testująca PolyCompose dla:
 * p wielomian x0, count równe 1, x[0] wielomian stały. */
static void test_PolyCompose_x_one_coeff(void **state) {
    (void)state;

    poly_coeff_t c = 5;
    Mono m = MonoFromCoeff(1, 1);
    Poly p = PolyAddMonos(1, &m);
    Poly x[1];
    x[0] = PolyFromCoeff(c);

    Poly res = PolyCompose(&p, 1, x);
    assert_true(PolyIsEq(&res, x));
    PolyDestroy(&p);
}

/* Funkcja testująca PolyCompose dla:
 * p wielomian x0, count równe 1, x[0] wielomian x0. */
static void test_PolyCompose_x_one_x(void **state) {
    (void)state;

    Mono m1 = MonoFromCoeff(1, 1);
    Mono m2 = MonoFromCoeff(1, 1);
    Poly p = PolyAddMonos(1, &m1);
    Poly x[1];
    x[0] = PolyAddMonos(1, &m2);

    Poly res = PolyCompose(&p, 1, x);
    assert_true(PolyIsEq(&res, &p));
    PolyDestroy(&p);
    PolyDestroy(x);
    PolyDestroy(&res);
}

static void test_drukuj(void **state) {
    (void)state;

    Drukuj();
    assert_string_equal(printf_buffer, "Wydruk testowy\n");

}

static void test_foo(void **state) {
    (void)state;

    assert_string_equal(printf_buffer, "");


}

static void test_foo_2(void **state) {
    (void)state;

    //printf("START TEST\n");
    init_input_stream("COMPOSE");
    assert_int_equal(calc_poly_main(), 0);
    //assert_string_equal(fprintf_buffer, "ERROR 1 WRONG COUNT\n");
    //init_input_stream("aaa");
    //printf("************* INSIDE TEST\n");


}

static char *argv[1];
static int argc;

/************* TEST FIXTURES ********************/


/**
 * Funkcja wołana przed każdym testem korzystającym z stdout lub stderr.
 */
static int test_setup(void **state) {
    (void)state;

    memset(fprintf_buffer, 0, sizeof(fprintf_buffer));
    memset(printf_buffer, 0, sizeof(printf_buffer));
    printf_position = 0;
    fprintf_position = 0;

    return 0;
}

/**
 * Funkcja wołana po każdym teście.
 */
static int test_teardown(void **state) {
    (void)state;

    return 0;
}

/**
 * Funkcja wołana po każdej grupie testów.
 */
static int test_group_teardown(void **state) {
    (void)state;

    return 0;
}

/**
 * Funkcja wołana przed każdą grupą testów.
 */
static int test_group_setup(void **state) {
    (void)state;

    return 0;
}

int main() {
    const struct CMUnitTest tests1[] = {
            cmocka_unit_test(test_PolyCompose_zero_zero_null),
            cmocka_unit_test(test_PolyCompose_zero_one_coeff),
            cmocka_unit_test(test_PolyCompose_coeff_zero_null),
            cmocka_unit_test(test_PolyCompose_coeff_one_coeff),
            cmocka_unit_test(test_PolyCompose_x_zero_null),
            cmocka_unit_test(test_PolyCompose_x_one_coeff),
            cmocka_unit_test(test_PolyCompose_x_one_x)
    };

    const struct CMUnitTest tests2[] = {
            cmocka_unit_test(test_PolyCompose_zero_zero_null),
            cmocka_unit_test(test_drukuj),
            cmocka_unit_test(test_foo)
            //cmocka_unit_test(),
            //cmocka_unit_test(),
            //cmocka_unit_test(),
            //cmocka_unit_test(),
            //cmocka_unit_test()
    };

    const struct CMUnitTest tests3[] = {
           // cmocka_unit_test_setup_teardown(test_foo, test_setup, test_teardown),
            cmocka_unit_test_setup_teardown(test_foo_2, test_setup, test_teardown)
    };


   cmocka_run_group_tests(tests1, test_setup, test_teardown);
    //cmocka_run_group_tests(tests2, test_setup, test_teardown);
    //cmocka_run_group_tests(tests3, test_group_setup, test_group_teardown);

    printf("END printfbuffer = [%s]\n", printf_buffer);
    printf("END fprintfbuffer = [%s]\n", fprintf_buffer);

    return 0;
}