#ifndef _ZEROCONF_H_
#define _ZEROCONF_H_

int getZeroconfHostPort(CPhidgetRemoteHandle networkInfo);
int cancelPendingZeroconfLookups(CPhidgetRemoteHandle networkInfo);
int refreshZeroconfSBC(CPhidgetSBCHandle sbc);
int refreshZeroconfPhidget(CPhidgetHandle phid);
int InitializeZeroconf();
int UninitializeZeroconf();

/* Internal version of .local lookups for SBC */
#ifdef ZEROCONF_LOOKUP
struct hostent *
mdns_gethostbyname (const char *name);
struct hostent *
mdns_gethostbyname2 (const char *name, int af);
#endif

#endif
