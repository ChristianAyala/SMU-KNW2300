#include "../stdafx.h"
#include "phidget_jni.h"
#include "com_phidgets_BridgePhidget.h"
#include "../cphidgetbridge.h"

EVENT_VARS(bridgeData, BridgeData)

JNI_LOAD(bridge, Bridge)
	EVENT_VAR_SETUP(bridge, bridgeData, BridgeData, ID, V)
}

EVENT_HANDLER_INDEXED(Bridge, bridgeData, BridgeData, 
					  CPhidgetBridge_set_OnBridgeData_Handler, double)

JNI_CREATE(Bridge)

JNI_GETFUNC(Bridge, InputCount, InputCount, jint)
JNI_INDEXED_GETFUNC(Bridge, BridgeValue, BridgeValue, jdouble)
JNI_INDEXED_GETFUNC(Bridge, BridgeMin, BridgeMin, jdouble)
JNI_INDEXED_GETFUNC(Bridge, BridgeMax, BridgeMax, jdouble)
JNI_INDEXED_GETFUNCBOOL(Bridge, Enabled, Enabled)
JNI_INDEXED_SETFUNC(Bridge, Enabled, Enabled, jboolean)

JNIEXPORT jint JNICALL
Java_com_phidgets_BridgePhidget_getGain(JNIEnv *env, jobject obj, jint index)
{
	CPhidgetBridgeHandle h = (CPhidgetBridgeHandle)(uintptr_t)
	    (*env)->GetLongField(env, obj, handle_fid);
	int error;
	CPhidgetBridge_Gain v;
	if ((error = CPhidgetBridge_getGain(h, index, &v)))
		PH_THROW(error);
	return (jint)v;
}

JNI_INDEXED_SETFUNC(Bridge, Gain, Gain, jint)
JNI_GETFUNC(Bridge, DataRate, DataRate, jint)
JNI_SETFUNC(Bridge, DataRate, DataRate, jint)
JNI_GETFUNC(Bridge, DataRateMin, DataRateMin, jint)
JNI_GETFUNC(Bridge, DataRateMax, DataRateMax, jint)