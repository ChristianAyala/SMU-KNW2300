#ifndef _PLIST_H_
#define _PLIST_H_

typedef struct plist_node plist_node_t;

int plist_contains(void *k, plist_node_t *root, void **nodeval);
int plist_remove(void *k, plist_node_t **root, void **ov);
int plist_add(void *k, void *v, plist_node_t **root);
void plist_clear(plist_node_t **root);
int plist_walk(plist_node_t *start, int(*func)(const void *k, const void *v,
    void *arg), void *arg);

#endif
