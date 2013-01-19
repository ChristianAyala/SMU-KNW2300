#ifndef __CSOCKETEVENTS
#define __CSOCKETEVENTS

#include "cphidget.h"
#include "regex.h"
#include "utils/utils.h"
#include "cphidgetlist.h"
#include "pdictclient.h"
#ifdef USE_ZEROCONF
#include "dns_sd.h"
#endif

extern regex_t phidgetsetex;
extern regex_t managerex;
extern regex_t managervalex;

void network_phidget_event_handler(const char *key, const char *val, unsigned int len, pdict_reason_t reason, void *ptr);
void network_manager_event_handler(const char *key, const char *val, unsigned int len, pdict_reason_t, void *ptr);
void network_heartbeat_event_handler(const char *key, const char *val, unsigned int len, pdict_reason_t reason, void *ptr);

#endif
