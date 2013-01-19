#include "../stdafx.h"
#include "phidget_jni.h"
#include "com_phidgets_AccelerometerPhidget.h"
#include "../cphidgetaccelerometer.h"

EVENT_VARS(accelerationChange, AccelerationChange)

JNI_LOAD(accel, Accelerometer)
	EVENT_VAR_SETUP(accel, accelerationChange, AccelerationChange, ID, V)
}

EVENT_HANDLER_INDEXED(Accelerometer, accelerationChange, AccelerationChange, 
					  CPhidgetAccelerometer_set_OnAccelerationChange_Handler, double)

JNI_CREATE(Accelerometer)
JNI_INDEXED_GETFUNC(Accelerometer, AccelerationChangeTrigger, AccelerationChangeTrigger, jdouble)
JNI_INDEXED_SETFUNC(Accelerometer, AccelerationChangeTrigger, AccelerationChangeTrigger, jdouble)
JNI_INDEXED_GETFUNC(Accelerometer, AccelerationMax, AccelerationMax, jdouble)
JNI_INDEXED_GETFUNC(Accelerometer, AccelerationMin, AccelerationMin, jdouble)
JNI_INDEXED_GETFUNC(Accelerometer, Acceleration, Acceleration, jdouble)
JNI_GETFUNC(Accelerometer, AxisCount, AxisCount, jint)
