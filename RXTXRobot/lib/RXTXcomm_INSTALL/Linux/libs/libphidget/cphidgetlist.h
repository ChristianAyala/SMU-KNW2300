#ifndef __CPHIDGETLIST
#define __CPHIDGETLIST

typedef struct _CList
{
	struct _CList *next;
	void *element;
} CList, *CListHandle;

int CList_addToList(CListHandle *list, void *element, 
	int (*compare_fptr)(void *element1, void *element2));
int CList_removeFromList(CListHandle *list, void *element, 
	int (*compare_fptr)(void *element1, void *element2),
	int freeDevice, void (*free_fptr)(void *element));
int CList_emptyList(CListHandle *list, int freeDevices, void (*free_fptr)(void *element));
int CList_findInList(CListHandle list, void *element, 
	int (*compare_fptr)(void *element1, void *element2), void **found_element);

#endif
