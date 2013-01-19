#ifndef __CPHIDGETSBC
#define __CPHIDGETSBC
#include "cphidget.h"
#include "cphidgetmanager.h"

DPHANDLE(SBC)
DPHANDLE(SBCManager)

#ifndef EXTERNALPROTO

typedef struct _CPhidgetSBC
{
	CPhidgetRemoteHandle networkInfo;
	int txtver;
	char fversion[13];
	short hversion;
	char mac[18];
	char hostname[129];
	char deviceName[255];
} CPhidgetSBC;

typedef struct _CPhidgetSBCList
{
	struct _CPhidgetSBCList *next;
	CPhidgetSBC *sbc;
} CPhidgetSBCList, *CPhidgetSBCListHandle;

typedef struct _CPhidgetSBCManager
{
	int mdns;					//true if mdns, false if not
	int(CCONV *fptrError)(CPhidgetSBCManagerHandle , void *, int, const char *);
	void *fptrErrorptr;
	CPhidgetManagerState state;
	int (CCONV *fptrAttachChange)(CPhidgetSBCHandle sbc, void *userPtr);
	void *fptrAttachChangeptr;
	int (CCONV *fptrDetachChange)(CPhidgetSBCHandle sbc, void *userPtr);
	void *fptrDetachChangeptr;
} CPhidgetSBCManager;

typedef struct _CPhidgetSBCManagerList
{
	struct _CPhidgetSBCManagerList *next;
	CPhidgetSBCManager *sbcm;
} CPhidgetSBCManagerList, *CPhidgetSBCManagerListHandle;

int CCONV CPhidgetSBC_areEqual(void *arg1, void *arg2);
int CCONV CPhidgetSBC_areExtraEqual(void *arg1, void *arg2);
void CCONV CPhidgetSBC_free(void *arg);
int CCONV CPhidgetSBC_create(CPhidgetSBCHandle *sbc);
int CCONV CPhidgetSBC_delete(CPhidgetSBCHandle sbc);

#endif

PHIDGET21_API int CCONV CPhidgetSBCManager_create(CPhidgetSBCManagerHandle *sbcm);
PHIDGET21_API int CCONV CPhidgetSBCManager_start(CPhidgetSBCManagerHandle sbcm);
PHIDGET21_API int CCONV CPhidgetSBCManager_stop(CPhidgetSBCManagerHandle sbcm);
PHIDGET21_API int CCONV CPhidgetSBCManager_delete(CPhidgetSBCManagerHandle sbcm);
PHIDGET21_API int CCONV CPhidgetSBCManager_set_OnAttach_Handler(CPhidgetSBCManagerHandle sbcm, int (CCONV *fptr)(CPhidgetSBCHandle sbc, void *userPtr), void *userPtr);
PHIDGET21_API int CCONV CPhidgetSBCManager_set_OnDetach_Handler(CPhidgetSBCManagerHandle sbcm, int (CCONV *fptr)(CPhidgetSBCHandle sbc, void *userPtr), void *userPtr);
PHIDGET21_API int CCONV CPhidgetSBCManager_getAttachedSBCs(CPhidgetSBCManagerHandle sbcm, CPhidgetSBCHandle *sbcArray[], int *count);
PHIDGET21_API int CCONV CPhidgetSBCManager_set_OnError_Handler(CPhidgetSBCManagerHandle sbcm, int(CCONV *fptr)(CPhidgetSBCManagerHandle sbcm, void *userPtr, int errorCode, const char *errorString), void *userPtr);

PHIDGET21_API int CCONV CPhidgetSBC_getFirmwareVersion(CPhidgetSBCHandle sbc, const char **firmwareVersion);
PHIDGET21_API int CCONV CPhidgetSBC_getHardwareVersion(CPhidgetSBCHandle sbc, int *hardwareVersion);
PHIDGET21_API int CCONV CPhidgetSBC_getMacAddress(CPhidgetSBCHandle sbc, const char **macAddress);
PHIDGET21_API int CCONV CPhidgetSBC_getAddress(CPhidgetSBCHandle sbc, const char **ipAddr);
PHIDGET21_API int CCONV CPhidgetSBC_getHostname(CPhidgetSBCHandle sbc, const char **hostname);
PHIDGET21_API int CCONV CPhidgetSBC_getDeviceName(CPhidgetSBCHandle sbc, const char **name);

#endif

