/**
 * LIST.C
 * Created by Michał Szafraniuk (ms219673)
 * MIMUW/IPP: zadanie1("Drzewo") marzec 2017
 */

#include <stdio.h>
#include <assert.h>
#include <stdlib.h>
#include "list.h"

typedef struct list {
    ListNodePtr first;
    ListNodePtr last;
    void *owner_adr; /* points to the corrresponding tree node i.e. a parent of the list */
} List;

typedef struct list_node {
    void *tnode_adr; /* points to the corresponding tree node */
    ListNodePtr prev;
    ListNodePtr next;
    ListPtr list_adr;  /* points to the wrapping list,
                        * valid only for the first and last node
                        * otherwise is NULL */
} ListNode;

ListPtr list_init(void *owner) {
    ListPtr list = (ListPtr) malloc(sizeof(List));
    assert(list);
    list->first = NULL;
    list->last = NULL;
    list->owner_adr = owner;
    return list;
}

ListNodePtr list_first(ListPtr list) {
    assert(list);
    return list->first;
}

ListNodePtr list_last(ListPtr list) {
    assert(list);
    return list->last;
}

ListNodePtr list_node_prev(ListNodePtr lnode) {
    assert(lnode);
    return lnode->prev;
}

ListNodePtr list_node_next(ListNodePtr lnode) {
    assert(lnode);
    return lnode->next;
}

void *list_node_tnode_adr(ListNodePtr lnode) {
    if (lnode == NULL)
        return NULL;
    return lnode->tnode_adr;
}

void list_set_owner(ListPtr list, void *owner) {
    assert(list);
    list->owner_adr = owner;
}

ListNodePtr list_insert_end(ListPtr list, void *tnode_ptr) {
    ListNodePtr new_lnode = (ListNodePtr) malloc(sizeof(ListNode));
    assert(new_lnode);

    new_lnode->tnode_adr = tnode_ptr;
    new_lnode->next = NULL;
    new_lnode->list_adr = list;
    if (list->first == NULL) {
        new_lnode->prev = NULL;
        list->first = new_lnode;
    }
    else {
        new_lnode->prev = list->last;
        list->last->next = new_lnode;
        if (list->last->prev != NULL)
            list->last->list_adr = NULL;
    }
    list->last = new_lnode;

    return new_lnode;
}

void list_delete_node(ListNodePtr lnode) {
    if (lnode == NULL) {
        return;
    }

    ListPtr l;
    if (lnode->prev == NULL) {
        l = lnode->list_adr;
        assert(l);
        l->first = lnode->next;
        if (lnode->next != NULL)
            lnode->next->list_adr = l;
    }
    else
        lnode->prev->next = lnode->next;

    if (lnode->next == NULL) {
        l = lnode->list_adr;
        l->last = lnode->prev;
        if (lnode->prev != NULL)
            lnode->prev->list_adr = l;
    }
    else
        lnode->next->prev = lnode->prev;
    free(lnode);
}

void list_emplace(ListNodePtr lnode, ListPtr put_list) {
    assert(lnode);
    assert(put_list);
    ListPtr base_list;

    if (lnode->prev != NULL) {
        if (put_list->first == NULL)
            lnode->prev->next = lnode->next;
        else {
            lnode->prev->next = put_list->first;
            put_list->first->prev = lnode->prev;
            put_list->first->list_adr = NULL;
        }
    }
    else {
        assert(lnode->list_adr != NULL);
        base_list = lnode->list_adr;
        if (put_list->first == NULL) {
            base_list->first = lnode->next;
            if (lnode->next != NULL)
                lnode->next->list_adr = base_list;
        }
        else {
            base_list->first = put_list->first;
            put_list->first->list_adr = base_list;
        }
    }

    if (lnode->next != NULL) {
        if (put_list->first == NULL)
            lnode->next->prev = lnode->prev;
        else {
            lnode->next->prev = put_list->last;
            put_list->last->next = lnode->next;
            if (put_list->last->prev != NULL)
                put_list->last->list_adr = NULL;
        }
    }
    else {
        assert(lnode->list_adr != NULL);
        base_list = lnode->list_adr;
        if (put_list->last == NULL) {
            base_list->last = lnode->prev;
            if (lnode->prev != NULL)
                lnode->prev->list_adr = base_list;
        }
        else {
            base_list->last = put_list->last;
            put_list->last->list_adr = base_list;
        }
    }

    free(lnode);
    free(put_list);
}

ListPtr list_cut(ListPtr base_list, ListNodePtr cut_lnode, void *new_list_data) {
    assert(base_list && cut_lnode);
    ListPtr new_list = list_init(new_list_data);
    assert(new_list);

    new_list->owner_adr = new_list_data;
    new_list->first = cut_lnode->next;
    if (cut_lnode->next != NULL) {
        cut_lnode->next->list_adr = new_list;
        new_list->last = base_list->last;
        new_list->last->list_adr = new_list;
    }
    base_list->last = cut_lnode;
    cut_lnode->list_adr = base_list;
    cut_lnode->next = NULL;
    if (new_list->first != NULL)
        new_list->first->prev = NULL;

    return new_list;
}