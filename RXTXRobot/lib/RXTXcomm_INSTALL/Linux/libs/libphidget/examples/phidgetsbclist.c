/*
 *  phidgetsbclist.c
 *  
 *  Displays a dynamic list of PhidgetSBCs on the network.
 *
 *  Created by Patrick Mcneil on 22/08/08.
 *  Copyright 2008 Phidgets Inc.. All rights reserved.
 *
 */

#include <stdio.h>
#include <phidget21.h>

//Define these here because they are not in the standard .h file
typedef struct _CPhidgetSBC *CPhidgetSBCHandle;
typedef struct _CPhidgetSBCManager *CPhidgetSBCManagerHandle;
int CPhidgetSBCManager_create(CPhidgetSBCManagerHandle *sbcm);
int CPhidgetSBCManager_start(CPhidgetSBCManagerHandle sbcm);
int CPhidgetSBCManager_stop(CPhidgetSBCManagerHandle sbcm);
int CPhidgetSBCManager_delete(CPhidgetSBCManagerHandle sbcm);
int CPhidgetSBCManager_set_OnAttach_Handler(CPhidgetSBCManagerHandle sbcm, int ( *fptr)(CPhidgetSBCHandle sbc, void *userPtr), void *userPtr);
int CPhidgetSBCManager_set_OnDetach_Handler(CPhidgetSBCManagerHandle sbcm, int ( *fptr)(CPhidgetSBCHandle sbc, void *userPtr), void *userPtr);int CPhidgetSBCManager_set_OnError_Handler(CPhidgetSBCManagerHandle sbcm, int( *fptr)(CPhidgetSBCManagerHandle sbcm, void *userPtr, int errorCode, const char *errorString), void *userPtr);
int CPhidgetSBC_getFirmwareVersion(CPhidgetSBCHandle sbc, const char **firmwareVersion);
int CPhidgetSBC_getHardwareVersion(CPhidgetSBCHandle sbc, int *hardwareVersion);
int CPhidgetSBC_getMacAddress(CPhidgetSBCHandle sbc, const char **macAddress);
int CPhidgetSBC_getAddress(CPhidgetSBCHandle sbc, const char **ipAddr);
int CPhidgetSBC_getHostname(CPhidgetSBCHandle sbc, const char **hostname);
int CPhidgetSBC_getDeviceName(CPhidgetSBCHandle sbc, const char **name);


int display_device_info(CPhidgetSBCHandle sbc)
{
	int hardwareVersion;
	const char *firmwareVersion, *macAddress, *address, *hostname, *name;
	
	CPhidgetSBC_getFirmwareVersion(sbc, &firmwareVersion);
	CPhidgetSBC_getHardwareVersion(sbc, &hardwareVersion);
	CPhidgetSBC_getMacAddress(sbc, &macAddress);
	CPhidgetSBC_getHostname(sbc, &hostname);
	CPhidgetSBC_getDeviceName(sbc, &name);
	if(CPhidgetSBC_getAddress(sbc, &address) != EPHIDGET_OK)
		address = NULL;
	
	printf("  Device Name:      %s\n",name);
	printf("  MAC Address:      %s\n",macAddress);
	printf("  Address:          %s\n",address?address:"(Unknown)");
	printf("  Hostname:         %s\n",hostname);
	printf("  Hardware Version: %d\n",hardwareVersion);
	printf("  Firmware Version: %s\n",firmwareVersion);
	
	return 0;
}

int attach(CPhidgetSBCHandle sbc, void *userPtr)
{
	printf("\n===ATTACH===\n");
	display_device_info(sbc);
	return 0;
}

int detach(CPhidgetSBCHandle sbc, void *userPtr)
{
	printf("\n===DETACH===\n");
	display_device_info(sbc);
	return 0;
}

int error(CPhidgetSBCManagerHandle sbcm, void *userPtr, int errorCode, const char *errorString)
{
	printf("Error Event: (%d) %s\n", errorCode, errorString);
	return 0;
}

int main(int argc, char *argv[])
{
	int result;
	CPhidgetSBCManagerHandle sbcm;
	
	CPhidgetSBCManager_create(&sbcm);
	
	CPhidgetSBCManager_set_OnAttach_Handler(sbcm, attach, NULL);
	CPhidgetSBCManager_set_OnDetach_Handler(sbcm, detach, NULL);
	CPhidgetSBCManager_set_OnError_Handler(sbcm, error, NULL);
	
	result = CPhidgetSBCManager_start(sbcm);
	
	if(result != EPHIDGET_OK)
	{
		const char *err;
		CPhidget_getErrorDescription(result, &err);
		printf("Error: (%d) %s\n", result, err);
		return 1;
	}
	
	printf("Press any key to end\n");
	getchar();
	printf("Closing...\n");
	
	CPhidgetSBCManager_stop(sbcm);
	CPhidgetSBCManager_delete(sbcm);
	
	return 0;
}

