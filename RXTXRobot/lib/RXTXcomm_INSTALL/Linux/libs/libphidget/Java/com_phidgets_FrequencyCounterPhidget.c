#include "../stdafx.h"
#include "phidget_jni.h"
#include "com_phidgets_FrequencyCounterPhidget.h"
#include "../cphidgetfrequencycounter.h"

EVENT_VARS(frequencyCounterCount, FrequencyCounterCount)

JNI_LOAD(frequencyCounter, FrequencyCounter)
	EVENT_VAR_SETUP(frequencyCounter, frequencyCounterCount, FrequencyCounterCount, III, V)
}

EVENT_HANDLER_INDEXED2(FrequencyCounter, frequencyCounterCount, FrequencyCounterCount, 
					  CPhidgetFrequencyCounter_set_OnCount_Handler, int, int)

JNI_CREATE(FrequencyCounter)

JNI_GETFUNC(FrequencyCounter, FrequencyInputCount, FrequencyInputCount, jint)
JNI_INDEXED_GETFUNC(FrequencyCounter, Frequency, Frequency, jdouble)
JNI_INDEXED_GETFUNC(FrequencyCounter, TotalTime, TotalTime, jlong)
JNI_INDEXED_GETFUNC(FrequencyCounter, TotalCount, TotalCount, jlong)
JNI_INDEXED_GETFUNC(FrequencyCounter, Timeout, Timeout, jint)
JNI_INDEXED_SETFUNC(FrequencyCounter, Timeout, Timeout, jint)
JNI_INDEXED_GETFUNCBOOL(FrequencyCounter, Enabled, Enabled)
JNI_INDEXED_SETFUNC(FrequencyCounter, Enabled, Enabled, jboolean)

JNIEXPORT jint JNICALL
Java_com_phidgets_FrequencyCounterPhidget_getFilter(JNIEnv *env, jobject obj, jint index)
{
	CPhidgetFrequencyCounterHandle h = (CPhidgetFrequencyCounterHandle)(uintptr_t)
	    (*env)->GetLongField(env, obj, handle_fid);
	int error;
	CPhidgetFrequencyCounter_FilterType v;
	if ((error = CPhidgetFrequencyCounter_getFilter(h, index, &v)))
		PH_THROW(error);
	return (jint)v;
}

JNI_INDEXED_SETFUNC(FrequencyCounter, Filter, Filter, jint)

JNIEXPORT void JNICALL
Java_com_phidgets_FrequencyCounterPhidget_reset(JNIEnv *env, jobject obj, jint index)
{
	CPhidgetFrequencyCounterHandle h = (CPhidgetFrequencyCounterHandle)(uintptr_t)
	    (*env)->GetLongField(env, obj, handle_fid);
	int error;

	if ((error = CPhidgetFrequencyCounter_reset(h, index)))
		PH_THROW(error);
}