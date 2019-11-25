/**
 * LIST.H
 * Created by Michał Szafraniuk (ms219673)
 * MIMUW/IPP: zadanie1("Drzewo") marzec 2017
 */

#ifndef LIST_H
#define LIST_H

typedef struct list List;
typedef struct list *ListPtr;
typedef struct list_node ListNode;
typedef struct list_node *ListNodePtr;

/* initializes the list with a pointer to its owner (corresponing tree node) */
ListPtr list_init(void *);

/* returns the pointer to the first node of the list or NULL if
 * the list is empty */
ListNodePtr list_first(ListPtr);

/* returns the pointer to the last node of the list or NULL if
 * the list is empty */
ListNodePtr list_last(ListPtr);

/* returns the pointer to the prev element in the list or NULL if
 * the arg element is the last one */
ListNodePtr list_node_prev(ListNodePtr);

/* returns the pointer to the next element in the list or NULL if
 * the arg element is the last one */
ListNodePtr list_node_next(ListNodePtr);

/* returns the pointer to the address of the corresponding tree node or
 * NULL if the argument list node is NULL */
void *list_node_tnode_adr(ListNodePtr lnode);

/* sets the owner of the list */
void list_set_owner(ListPtr, void *);

/* inserts a new list node at the end of the given list */
ListNodePtr list_insert_end(ListPtr, void *);

/* deletes a list node */
void list_delete_node(ListNodePtr);

/* emplaces the argument list into the list node slot, deleting the given node */
void list_emplace(ListNodePtr, ListPtr);

/* cuts the first argument base list at list node argument, returns
 * the pointer to the new cut-off part of the list */
ListPtr list_cut(ListPtr, ListNodePtr, void *);

#endif /* LIST_H */
