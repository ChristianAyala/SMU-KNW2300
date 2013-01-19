#include "../stdafx.h"
#include "phidget_jni.h"
#include "com_phidgets_InterfaceKitPhidget.h"
#include "../cphidgetinterfacekit.h"

EVENT_VARS(inputChange, InputChange)
EVENT_VARS(outputChange, OutputChange)
EVENT_VARS(sensorChange, SensorChange)

JNI_LOAD(ifkit, InterfaceKit)
	EVENT_VAR_SETUP(ifkit, inputChange, InputChange, IZ, V)
	EVENT_VAR_SETUP(ifkit, outputChange, OutputChange, IZ, V)
	EVENT_VAR_SETUP(ifkit, sensorChange, SensorChange, II, V)
}

EVENT_HANDLER_INDEXED(InterfaceKit, inputChange, InputChange, CPhidgetInterfaceKit_set_OnInputChange_Handler, int)
EVENT_HANDLER_INDEXED(InterfaceKit, outputChange, OutputChange, CPhidgetInterfaceKit_set_OnOutputChange_Handler, int)
EVENT_HANDLER_INDEXED(InterfaceKit, sensorChange, SensorChange, CPhidgetInterfaceKit_set_OnSensorChange_Handler, int)

JNI_CREATE(InterfaceKit)
JNI_GETFUNC(InterfaceKit, OutputCount, OutputCount, jint)
JNI_GETFUNC(InterfaceKit, InputCount, InputCount, jint)
JNI_GETFUNC(InterfaceKit, SensorCount, SensorCount, jint)
JNI_INDEXED_GETFUNCBOOL(InterfaceKit, InputState, InputState)
JNI_INDEXED_GETFUNCBOOL(InterfaceKit, OutputState, OutputState)
JNI_INDEXED_GETFUNC(InterfaceKit, SensorValue, SensorValue, jint)
JNI_INDEXED_GETFUNC(InterfaceKit, SensorRawValue, SensorRawValue, jint)
JNI_INDEXED_GETFUNC(InterfaceKit, DataRateMin, DataRateMin, jint)
JNI_INDEXED_GETFUNC(InterfaceKit, DataRateMax, DataRateMax, jint)
JNI_INDEXED_GETFUNC(InterfaceKit, DataRate, DataRate, jint)
JNI_INDEXED_GETFUNC(InterfaceKit, SensorChangeTrigger, SensorChangeTrigger, jint)
JNI_GETFUNCBOOL(InterfaceKit, Ratiometric, Ratiometric)
JNI_INDEXED_SETFUNC(InterfaceKit, OutputState, OutputState, jboolean)
JNI_INDEXED_SETFUNC(InterfaceKit, DataRate, DataRate, jint)
JNI_INDEXED_SETFUNC(InterfaceKit, SensorChangeTrigger, SensorChangeTrigger, jint)
JNI_SETFUNC(InterfaceKit, Ratiometric, Ratiometric, jboolean)
