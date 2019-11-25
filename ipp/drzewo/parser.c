/**
 * PARSER.C
 * Created by Michał Szafraniuk (ms219673)
 * MIMUW/IPP: zadanie1("Drzewo") marzec 2017
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include "parser.h"

const char opliteral_add[] = "ADD_NODE";                    /* op_id = 1 */
const char opliteral_rightmost[] = "RIGHTMOST_CHILD";       /* op_id = 2 */
const char opliteral_delete_node[] = "DELETE_NODE";         /* op_id = 3 */
const char opliteral_delete_subtree[] = "DELETE_SUBTREE";   /* op_id = 4 */
const char opliteral_split[] = "SPLIT_NODE";                /* op_id = 5 */

enum op_id {
    ADD_ID = 1,
    RIGHTMOST_CHILD_ID = 2,
    DELETE_NODE_ID = 3,
    DELETE_SUBTREE_ID = 4,
    SPLIT_NODE_ID = 5
};

typedef struct operation {
    int op_id;
    int op_param_1;
    int op_param_2;
} Operation;

Operation *init_operation() {
    Operation *op = (Operation *) malloc(sizeof(Operation));
    assert(op);

    return op;
}

int parser_operation_id(Operation *op) {

    return op->op_id;
}

int parser_operation_param_1(Operation *op) {

    return op->op_param_1;
}

int parser_operation_param_2(Operation *op) {

    return op->op_param_2;
}

void set_operation(Operation *op, int a, int b, int c) {
    op->op_id = a;
    op->op_param_1 = b;
    op->op_param_2 = c;
}

int read_operation(FILE *f, Operation *op) {
    char line[MAX_CMD_LENGTH];
    char *token;
    const char separator[2] = " ";

    if (fgets(line, sizeof(line), f) != NULL) {
        token = strtok(line, separator);

        if (strcmp(token, opliteral_add) == 0) {
            set_operation(op, ADD_ID, atoi(strtok(NULL, separator)), -1);
            return 0;
        }

        if (strcmp(token, opliteral_rightmost) == 0) {
            set_operation(op, RIGHTMOST_CHILD_ID, atoi(strtok(NULL, separator)), -1);
            return 0;
        }

        if (strcmp(token, opliteral_delete_node) == 0) {
            set_operation(op, DELETE_NODE_ID, atoi(strtok(NULL, separator)), -1);
            return 0;
        }

        if (strcmp(token, opliteral_delete_subtree) == 0) {
            set_operation(op, DELETE_SUBTREE_ID, atoi(strtok(NULL, separator)), -1);
            return 0;
        }

        if (strcmp(token, opliteral_split) == 0) {
            set_operation(op, SPLIT_NODE_ID, atoi(strtok(NULL, separator)),
                          atoi(strtok(NULL, separator)));
            return 0;
        }

        return -1;
    }

    return -1;
}