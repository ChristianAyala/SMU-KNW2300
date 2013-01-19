#ifndef _PDICTCLIENT_H_
#define _PDICTCLIENT_H_

#include "pdict.h"

typedef struct pdc_session pdc_session_t;
typedef int pdc_listen_id_t;

int CCONV pdc_init(void);

pdc_session_t * CCONV 
pdc_session_alloc(int readfd, 
				  int(*readfunc)(int, void *, unsigned int, char *, int), 
				  int writefd,
				  int(*writefunc)(int, const void *, unsigned int, char *, int),
				  int(*closefunc)(int, char *, int), 
				  void *cleanupPtr,
				  void (*cleanupFunc)(void *));

void 
pdc_session_free(pdc_session_t *pdcs);

//int CCONV pdc_set(pdc_session_t *pdcs, const char *key, const char *val, int len, int remove_on_close, char *errdesc, int errlen);
void CCONV pdc_async_set(pdc_session_t *pdcs, 
						 const char *key, 
						 const char *val, 
						 int len, 
						 int remove_on_close, 
						 void (*error)(const char *errdesc, void *arg), 
						 void *arg);

pdc_listen_id_t CCONV 
pdc_listen(pdc_session_t *pdcs, 
		   const char *pattern, 
		   void (*cb)(const char *, const char *, unsigned int, pdict_reason_t, void *), 
		   void *ptr, 
		   char *errdesc, 
		   int errlen);

int CCONV 
pdc_disable_periodic_reports(pdc_session_t *pdc, 
							 char *errdesc, 
							 int errlen);

void CCONV 
pdc_async_disable_periodic_reports(pdc_session_t *pdc, 
								   void (*error)(const char *errdesc, void *arg), 
								   void *arg);

int CCONV 
pdc_enable_periodic_reports(pdc_session_t *pdc, 
							int periodms, 
							char *errdesc, 
							int errlen);

void CCONV 
pdc_async_enable_periodic_reports(pdc_session_t *pdc, 
								  int periodms, 
								  void (*error)(const char *errdesc, void *arg), 
								  void *arg);

int CCONV 
pdc_ignore(pdc_session_t *pdcs, 
		   pdc_listen_id_t id, 
		   char *errdesc, 
		   int errlen);

void CCONV 
pdc_async_ignore(pdc_session_t *pdcs, 
				 pdc_listen_id_t id, 
				 void (*error)(const char *errdesc, void *arg), 
				 void *arg);

int CCONV 
pdc_flush(pdc_session_t *pdc, 
		  char *errdesc, 
		  int errlen);

int CCONV 
pdc_quit(pdc_session_t *pdc, 
		 char *errdesc, 
		 int errlen);

int CCONV 
pdc_remove(pdc_session_t *pdc, 
		   const char *pattern, 
		   char *errdesc, 
		   int errlen);

void CCONV
pdc_async_remove(pdc_session_t *pdcs, 
				 const char *pattern, 
				 void (*error)(const char *errdesc, void *arg), 
				 void *arg);

int CCONV 
pdc_get(pdc_session_t *pdcs, 
		const char *pattern, 
		char *results, 
		int resultslen, 
		char *errdesc, 
		int errlen);

int CCONV 
pdc_get_server_session_id(pdc_session_t *pdc, 
						  int *id, 
						  char *errdesc, 
						  int errlen);

int CCONV 
pdc_readthread_join(pdc_session_t *pdcs, 
					void **status);

void CCONV 
pdc_async_authorize(pdc_session_t *pdcs, 
					const char *version, 
					char *password, 
					void (*success) (void *arg, void (*error)(const char *errdesc, void *arg)), 
					void (*error)(const char *errdesc, void *arg), 
					void *arg);

void
cleanup_pending(pdc_session_t *pdcs, void *arg);

void
wait_pending(pdc_session_t *pdcs);

#endif
