// InterfacekitTest.cpp : Defines the entry point for the console application.
//


#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <phidget21.h>

void display_generic_properties(CPhidgetHandle phid)
{
	int sernum, version;
	const char *deviceptr, *label;
	CPhidget_getDeviceType(phid, &deviceptr);
	CPhidget_getSerialNumber(phid, &sernum);
	CPhidget_getDeviceVersion(phid, &version);
	CPhidget_getDeviceLabel(phid, &label);

	printf("%s\n", deviceptr);
	printf("Version: %8d SerialNumber: %10d\n", version, sernum);
	printf("Label: %s\n", label);
	return;
}


int IFK_DetachHandler(CPhidgetHandle IFK, void *userptr)
{
	printf("Detach handler ran!\n");
	return 0;
}

int IFK_ErrorHandler(CPhidgetHandle IFK, void *userptr, int ErrorCode, const char *ErrorString)
{
	printf("Error handler: %d, %s\n", ErrorCode, ErrorString);
	return 0;
}

int IFK_OutputChangeHandler(CPhidgetInterfaceKitHandle IFK, void *userptr, int Index, int Value)
{
	printf("Output %d is %d\n", Index, Value);
	return 0;
}

int IFK_InputChangeHandler(CPhidgetInterfaceKitHandle IFK, void *userptr, int Index, int Value)
{
	printf("Input %d is %d\n", Index, Value);
	return 0;
}

int IFK_SensorChangeHandler(CPhidgetInterfaceKitHandle IFK, void *userptr, int Index, int Value)
{
	printf("Sensor %d is %d\n", Index, Value);
	return 0;
}


int IFK_AttachHandler(CPhidgetHandle IFK, void *userptr)
{
	//CPhidgetInterfaceKit_setSensorChangeTrigger((CPhidgetInterfaceKitHandle)IFK, 0, 0);
	printf("Attach handler ran!\n");
	return 0;
}

int test_interfacekit()
{
	int numInputs, numOutputs, numSensors;
	int err;
	CPhidgetInterfaceKitHandle IFK = 0;

	CPhidget_enableLogging(PHIDGET_LOG_VERBOSE, NULL);
	
	CPhidgetInterfaceKit_create(&IFK);

	CPhidgetInterfaceKit_set_OnInputChange_Handler(IFK, IFK_InputChangeHandler, NULL);
	CPhidgetInterfaceKit_set_OnOutputChange_Handler(IFK, IFK_OutputChangeHandler, NULL);
	CPhidgetInterfaceKit_set_OnSensorChange_Handler(IFK, IFK_SensorChangeHandler, NULL);
	CPhidget_set_OnAttach_Handler((CPhidgetHandle)IFK, IFK_AttachHandler, NULL);
	CPhidget_set_OnDetach_Handler((CPhidgetHandle)IFK, IFK_DetachHandler, NULL);
	CPhidget_set_OnError_Handler((CPhidgetHandle)IFK, IFK_ErrorHandler, NULL);
	
	CPhidget_open((CPhidgetHandle)IFK, -1);

	//wait 5 seconds for attachment
	if((err = CPhidget_waitForAttachment((CPhidgetHandle)IFK, 0)) != EPHIDGET_OK )
	{
		const char *errStr;
		CPhidget_getErrorDescription(err, &errStr);
		printf("Error waiting for attachment: (%d): %s\n",err,errStr);
		goto exit;
	}
	
	display_generic_properties((CPhidgetHandle)IFK);
	CPhidgetInterfaceKit_getOutputCount((CPhidgetInterfaceKitHandle)IFK, &numOutputs);
	CPhidgetInterfaceKit_getInputCount((CPhidgetInterfaceKitHandle)IFK, &numInputs);
	CPhidgetInterfaceKit_getSensorCount((CPhidgetInterfaceKitHandle)IFK, &numSensors);
	
	CPhidgetInterfaceKit_setOutputState((CPhidgetInterfaceKitHandle)IFK, 0, 1);

	printf("Sensors:%d Inputs:%d Outputs:%d\n", numSensors, numInputs, numOutputs);
	
	//err = CPhidget_setDeviceLabel((CPhidgetHandle)IFK, "test");
	
	while(1)
	{
		sleep(1);
	}
	
	while(1)
	{
		CPhidgetInterfaceKit_setOutputState(IFK, 7, 1);
		CPhidgetInterfaceKit_setOutputState(IFK, 7, 0);
	}

	CPhidgetInterfaceKit_setOutputState(IFK, 0, 1);
	sleep(1);
	CPhidgetInterfaceKit_setOutputState(IFK, 0, 0);
	sleep(1);
	CPhidgetInterfaceKit_setOutputState(IFK, 0, 1);
	sleep(1);
	CPhidgetInterfaceKit_setOutputState(IFK, 0, 0);

	sleep(5);

exit:
	CPhidget_close((CPhidgetHandle)IFK);
	CPhidget_delete((CPhidgetHandle)IFK);

	return 0;
}

int main(int argc, char* argv[])
{
	test_interfacekit();
	return 0;
}

