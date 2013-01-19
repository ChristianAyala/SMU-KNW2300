#ifndef __CUSB
#define __CUSB

#include "cphidget.h"
#include "cphidgetlist.h"

#ifndef EXTERNALPROTO
int CUSBBuildList(CPhidgetList **curList);
int CUSBOpenHandle(CPhidgetHandle phid);
int CUSBCloseHandle(CPhidgetHandle phid);
int CUSBSetLabel(CPhidgetHandle phid, char *buffer);
void CUSBCleanup();
int CUSBSetupNotifications();
int CUSBRefreshLabelString(CPhidgetHandle phid);
int CUSBGetString(CPhidgetHandle phid, int index, char *str);
#if defined(_LINUX) && !defined(_ANDROID)
void CUSBUninit();
#else
int CUSBGetDeviceCapabilities(CPhidgetHandle phid, HANDLE DeviceHandle);
#endif
#endif

#ifdef _IPHONE
int CPhidgetManager_setupNotifications(CFRunLoopRef runloop);
int CPhidgetManager_teardownNotifications();
int reenumerateDevice(CPhidgetHandle phid);
#endif

PHIDGET21_API int CCONV CUSBReadPacket(CPhidgetHandle phidA, unsigned char *buffer);
PHIDGET21_API int CCONV CUSBSendPacket(CPhidgetHandle phidA, unsigned char *buffer);

#if defined(_ANDROID)
#include "com_phidgets_usb_Manager.h"
#endif

#endif
