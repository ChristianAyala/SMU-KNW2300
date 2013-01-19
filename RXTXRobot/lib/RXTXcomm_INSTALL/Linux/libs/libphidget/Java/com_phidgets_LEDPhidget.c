#include "../stdafx.h"
#include "phidget_jni.h"
#include "com_phidgets_LEDPhidget.h"
#include "../cphidgetled.h"

JNI_LOAD(accel, LED)
}

JNI_CREATE(LED)
JNI_INDEXED_GETFUNC(LED, DiscreteLED, DiscreteLED, jint)
JNI_INDEXED_SETFUNC(LED, DiscreteLED, DiscreteLED, jint)
JNI_INDEXED_GETFUNC(LED, Brightness, Brightness, jdouble)
JNI_INDEXED_SETFUNC(LED, Brightness, Brightness, jdouble)
JNI_INDEXED_GETFUNC(LED, CurrentLimit__I, CurrentLimitIndexed, jdouble)
JNI_INDEXED_SETFUNC(LED, CurrentLimit__ID, CurrentLimitIndexed, jdouble)
JNI_GETFUNC(LED, LEDCount, LEDCount, jint)

JNIEXPORT jint JNICALL
Java_com_phidgets_LEDPhidget_getCurrentLimit__(JNIEnv *env, jobject obj)
{
	CPhidgetLEDHandle h = (CPhidgetLEDHandle)(uintptr_t)
	    (*env)->GetLongField(env, obj, handle_fid);
	int error;
	CPhidgetLED_CurrentLimit v;
	if ((error = CPhidgetLED_getCurrentLimit(h, &v)))
		PH_THROW(error);
	return (jint)v;
}
JNI_SETFUNC(LED, CurrentLimit__I, CurrentLimit, jint)

JNIEXPORT jint JNICALL
Java_com_phidgets_LEDPhidget_getVoltage(JNIEnv *env, jobject obj)
{
	CPhidgetLEDHandle h = (CPhidgetLEDHandle)(uintptr_t)
	    (*env)->GetLongField(env, obj, handle_fid);
	int error;
	CPhidgetLED_Voltage v;
	if ((error = CPhidgetLED_getVoltage(h, &v)))
		PH_THROW(error);
	return (jint)v;
}
JNI_SETFUNC(LED, Voltage, Voltage, jint)