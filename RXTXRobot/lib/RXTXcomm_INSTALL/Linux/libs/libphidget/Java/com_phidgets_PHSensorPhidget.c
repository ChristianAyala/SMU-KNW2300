#include "../stdafx.h"
#include "phidget_jni.h"
#include "com_phidgets_PHSensorPhidget.h"
#include "../cphidgetphsensor.h"

EVENT_VARS(phChange, PHChange)

JNI_LOAD(ph, PHSensor)
	EVENT_VAR_SETUP(ph, phChange, PHChange, D, V)
}

EVENT_HANDLER(PHSensor, phChange, PHChange, 
			  CPhidgetPHSensor_set_OnPHChange_Handler, double)

JNI_CREATE(PHSensor)
JNI_GETFUNC(PHSensor, PHChangeTrigger, PHChangeTrigger, jdouble)
JNI_SETFUNC(PHSensor, PHChangeTrigger, PHChangeTrigger, jdouble)
JNI_GETFUNC(PHSensor, PH, PH, jdouble)
JNI_GETFUNC(PHSensor, PHMin, PHMin, jdouble)
JNI_GETFUNC(PHSensor, PHMax, PHMax, jdouble)
JNI_GETFUNC(PHSensor, Potential, Potential, jdouble)
JNI_GETFUNC(PHSensor, PotentialMin, PotentialMin, jdouble)
JNI_GETFUNC(PHSensor, PotentialMax, PotentialMax, jdouble)
JNI_SETFUNC(PHSensor, Temperature, Temperature, jdouble)
