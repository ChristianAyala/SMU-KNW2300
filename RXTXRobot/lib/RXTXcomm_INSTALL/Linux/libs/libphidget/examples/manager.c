/*
* manager.cpp
*
* Patrick McNeil - July 19, 2005
*
* Simple program to list all Phidgets that are added and removed from the system
*/

#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include "phidget21.h"
#include <signal.h>
		
int something;
static int exitMan = 0;

void sighandler (int n)
{
    printf ("got a signal. exiting.\n");
    exitMan = 1;
}

int gotAttach(CPhidgetHandle phid, void *ptr) {
	const char *id, *label;
	int serial, version;
	
	//print out some info
	CPhidget_getDeviceLabel(phid, &label);
	CPhidget_getSerialNumber(phid, &serial);
  	CPhidget_getDeviceVersion(phid, &version);
	CPhidget_getDeviceName(phid, &id);
	printf("Device Added: %s, Serial: %d, Version: %d Label: %s\n",id,serial,version,label);
	
	return 0;
}

int gotDetach(CPhidgetHandle phid, void *ptr) {
	char *id;
	int serial, version;
	
	//print out some info  
	CPhidget_getSerialNumber(phid, &serial);
  	CPhidget_getDeviceVersion(phid, &version);
	CPhidget_getDeviceName((CPhidgetHandle)phid,(const char **)&id);
	printf("Device Removed: %s, Serial: %d, Version: %d\n",id,serial,version);
	
	return 0;
}


int main() 
{
  CPhidgetManagerHandle phidm;
	
  //CPhidget_enableLogging(PHIDGET_LOG_VERBOSE, NULL);
	
  CPhidgetManager_create(&phidm);
  CPhidgetManager_set_OnAttach_Handler(phidm, gotAttach, NULL);
  CPhidgetManager_set_OnDetach_Handler(phidm, gotDetach, NULL);
  CPhidgetManager_open(phidm);
  //CPhidgetManager_openRemote(phidm, NULL, NULL);

  signal (SIGTERM, sighandler);
  signal (SIGINT, sighandler);

while(!exitMan)
  sleep(5);

  printf ("Removing resources.\n");
	 CPhidgetManager_close(phidm);
	 CPhidgetManager_delete(phidm);
  return 0;
}
