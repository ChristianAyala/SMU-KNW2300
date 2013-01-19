#ifndef _PDICT_IMPL_H_
#define _PDICT_IMPL_H_

#include "pdict.h"

struct pdict_listener;

/*
 * Represents a dictionary entry.
 * key and value strings, with a plist of listeners
 */
struct pdict_ent {
	const char *pde_key;
	const char *pde_val;
	plist_node_t *pde_listeners;
};

typedef struct pdict_listener {
	pdl_notify_func_t pdl_notify;
	void *pdl_arg;
} pdict_listener_t;

typedef struct pdict_persistent_listener {
	pdict_listener_t pdpl_l;
	regex_t pdpl_regex;
	int pdpl_new;
} pdict_persistent_listener_t;

/*
 * A Phidget Dictionary
 * contains a ptree of entries and a plist of persistent listeners
 */
struct pdict {
	ptree_node_t *pd_ents;
	plist_node_t *pd_persistent_listeners;
};

#endif
