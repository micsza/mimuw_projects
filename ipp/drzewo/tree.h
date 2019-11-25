/**
 * TREE.H
 * Created by Michał Szafraniuk (ms219673)
 * MIMUW/IPP: zadanie1("Drzewo") marzec 2017
 */

#ifndef TREE_H
#define TREE_H

#define MAX_TREE_SIZE (1000 * 1000)

typedef struct tree Tree;
typedef struct tree *TreePtr;
typedef struct tree_node TreeNode;
typedef struct tree_node *TreeNodePtr;

/* initializes the tree */
TreePtr tree_init();

/* returns the current size of the tree */
int tree_size(TreePtr);

/* adds a new tree node as a child of the parent given as 2nd argument */
void tree_add_node(TreePtr, int);

/* prints the rightmost child of the parent given as 2nd argument */
void tree_print_rightmost_child(TreePtr, int);

/* deletes a tree node */
void tree_delete_node(TreePtr, int);

/* deletes a subtree */
void tree_delete_subtree(TreePtr, int);

/* splits the nodes */
void tree_split_node(TreePtr, int, int);

/* deletes the tree freeing the allocatated memory */
void tree_free(TreePtr);

#endif /* TREE_H */
