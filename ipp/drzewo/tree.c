/**
 * TREE.C
 * Created by Michał Szafraniuk (ms219673)
 * MIMUW/IPP: zadanie1("Drzewo") marzec 2017
 */

#include <stdio.h>
#include <assert.h>
#include <stdlib.h>
#include "tree.h"
#include "list.h"

typedef struct tree {
    int size;           /* current tree size */
    int next_key;       /* next key to be assigned */
    TreeNodePtr tnodes_array[MAX_TREE_SIZE]; /* maps key into node pointer */
} Tree;

typedef struct tree_node {
    int key;
    ListPtr child_list;
    void *lnode_adr;    /* corresponding list node address on the parent list*/
} TreeNode;

TreePtr tree_init() {
    TreePtr tree = (TreePtr) malloc(sizeof(Tree));
    assert(tree);
    TreeNodePtr new_root = (TreeNodePtr) malloc(sizeof(TreeNode));
    assert(new_root);
    new_root->key = 0;
    new_root->child_list = list_init(new_root);
    new_root->lnode_adr = NULL;
    tree->size = 1;
    tree->next_key = 1;
    tree->tnodes_array[0] = new_root;

    return tree;
}

int tree_size(TreePtr tree) {
    assert(tree);

    return tree->size;
}

TreeNodePtr tree_init_node(int key) {
    TreeNodePtr new_tnode = (TreeNodePtr) malloc(sizeof(TreeNode));
    assert(new_tnode);
    new_tnode->key = key;
    new_tnode->child_list = list_init(new_tnode);
    new_tnode->lnode_adr = NULL;

    return new_tnode;
}

void tree_add_node(TreePtr tree, int parent_key) {
    TreeNodePtr parent = tree->tnodes_array[parent_key];
    assert(parent);
    TreeNodePtr new_tnode = tree_init_node(tree->next_key);
    assert(new_tnode);
    tree->tnodes_array[tree->next_key] = new_tnode;
    new_tnode->lnode_adr = list_insert_end(parent->child_list, new_tnode);
    tree->next_key++;
    tree->size++;
}

TreeNodePtr tree_rightmost_child(TreePtr tree, int parent_key) {
    TreeNodePtr tnode = tree->tnodes_array[parent_key];
    assert(tnode);
    ListNodePtr lnode = list_last(tnode->child_list);
    if (lnode == NULL)
        return NULL;

    return list_node_tnode_adr(lnode);
}

void tree_print_rightmost_child(TreePtr tree, int parent_key) {
    TreeNodePtr tnode = tree_rightmost_child(tree, parent_key);
    if (tnode == NULL)
        printf("-1\n");
    else
        printf("%d\n",tnode->key);
}

void tree_delete_node(TreePtr tree, int key) {
    assert(key != 0);
    TreeNodePtr tnode = tree->tnodes_array[key];
    assert(tnode);
    ListNodePtr lnode = tnode->lnode_adr;
    assert(lnode);
    list_emplace(lnode, tnode->child_list);

    tree->tnodes_array[key] = NULL;
    free(tnode);
    tree->size--;
}

void tree_delete_subtree_ptr(TreePtr tree, TreeNodePtr tnode) {
    assert(tnode);
    TreeNodePtr child_tnode;
    ListNodePtr child_lnode, child_lnode_iter;
    child_lnode = list_first(tnode->child_list);

    if (child_lnode == NULL)
        child_tnode = NULL;
    else
        child_tnode = list_node_tnode_adr(child_lnode);

    while (child_tnode != NULL) {
        child_lnode_iter = list_node_next(child_lnode);
        tree_delete_subtree_ptr(tree, child_tnode);
        if (child_lnode_iter == NULL)
            child_tnode = NULL;
        else {
            child_lnode = child_lnode_iter;
            child_tnode = list_node_tnode_adr(child_lnode);

        }
    }
    list_delete_node(tnode->lnode_adr);
    tree->tnodes_array[tnode->key] = NULL;
    tree->size--;
    free(tnode->child_list);
    free(tnode);
}

void tree_delete_subtree(TreePtr tree, int key) {
    TreeNodePtr tnode = tree->tnodes_array[key];
    assert(tnode);
    tree_delete_subtree_ptr(tree, tnode);
}

void tree_split_node(TreePtr tree, int parent_key, int split_key) {
    assert(split_key != 0);
    TreeNodePtr parent_tnode = tree->tnodes_array[parent_key];
    TreeNodePtr split_tnode = tree->tnodes_array[split_key];
    assert(parent_tnode);
    assert(split_tnode);

    TreeNodePtr new_tnode = tree_init_node(tree->next_key);
    ListPtr list = list_cut(parent_tnode->child_list, split_tnode->lnode_adr, new_tnode);
    free(new_tnode->child_list);
    new_tnode->child_list = list;
    list_set_owner(new_tnode->child_list, new_tnode);
    new_tnode->lnode_adr = list_insert_end(parent_tnode->child_list, new_tnode);

    tree->tnodes_array[tree->next_key] = new_tnode;
    tree->next_key++;
    tree->size++;
}

void tree_free_tnodes(TreePtr tree) {
    tree_delete_subtree(tree, 0);
}

void tree_free(TreePtr tree) {
    tree_free_tnodes(tree);
    free(tree);
}

