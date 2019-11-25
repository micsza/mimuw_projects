/**
 * SOLUTION.C
 * Created by Michał Szafraniuk (ms219673)
 * MIMUW/IPP: zadanie1("Drzewo") marzec 2017
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "tree.h"
#include "parser.h"

int main(int argc, char **argv) {
    int vflag = 0; /* The variable determining "NODE/NO NODE printing" mode */
    const char v_param[] = "-v";

    if (argc > 2) {
        perror("ERROR: invalid parameters\n");
        return(1);
    }

    if (argc == 2) {
        if (strcmp(argv[1], v_param) == 0) {
            vflag = 1;
        }
        else {
            printf("ERROR: invalid parameters\n");
            return(1);
        }
    }

    Operation *operation = init_operation();
    TreePtr tree = tree_init();

    while (read_operation(stdin, operation) == 0)  {
        switch (parser_operation_id(operation)) { //    operation->op_id) {
            case 1:
                tree_add_node(tree, parser_operation_param_1(operation));
                printf("OK\n");
                if (vflag == 1) fprintf(stderr, "NODES: %d\n", tree_size(tree));
                break;
            case 2:
                tree_print_rightmost_child(tree, parser_operation_param_1(operation));
                if (vflag == 1) fprintf(stderr, "NODES: %d\n", tree_size(tree));
                break;
            case 3:
                tree_delete_node(tree, parser_operation_param_1(operation));
                printf("OK\n");
                if (vflag == 1) fprintf(stderr, "NODES: %d\n", tree_size(tree));
                break;
            case 4:
                tree_delete_subtree(tree, parser_operation_param_1(operation));
                printf("OK\n");
                if (vflag == 1) fprintf(stderr, "NODES: %d\n", tree_size(tree));
                break;
            case 5:
                tree_split_node(tree, parser_operation_param_2(operation), parser_operation_param_1(operation));
                printf("OK\n");
                if (vflag == 1) fprintf(stderr, "NODES: %d\n", tree_size(tree));
                break;
            default:
                perror("Error: unrecognized operation label\n");
                return -1;
        }
    }

    tree_free(tree);
    free(operation);

    return 0;
}

