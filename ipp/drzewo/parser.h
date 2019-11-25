/**
 * PARSER.H
 * Created by Michał Szafraniuk (ms219673)
 * MIMUW/IPP: zadanie1("Drzewo") marzec 2017
 */

#ifndef PARSER_H
#define PARSER_H

#define MAX_CMD_LENGTH 64

typedef struct operation Operation;

/* initializes Operation struct */
Operation *init_operation();

/* returns operation id */
int parser_operation_id(Operation *);

/* returns operation parameter 1 */
int parser_operation_param_1(Operation *op);

/* returns operation parameter 2 */
int parser_operation_param_2(Operation *op);

/* reads operation from stream and returns -1 on failure */
int read_operation(FILE *, Operation *);

#endif /* PARSER_H */
