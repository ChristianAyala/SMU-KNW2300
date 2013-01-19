#include "../stdafx.h"
#include "phidget_jni.h"
#include "com_phidgets_WeightSensorPhidget.h"
#include "../cphidgetweightsensor.h"

EVENT_VARS(weightChange, WeightChange)

JNI_LOAD(weight, WeightSensor)
	EVENT_VAR_SETUP(weight, weightChange, WeightChange, D, V)
}

EVENT_HANDLER(WeightSensor, weightChange, WeightChange, 
			  CPhidgetWeightSensor_set_OnWeightChange_Handler, double)

JNI_CREATE(WeightSensor)
JNI_GETFUNC(WeightSensor, WeightChangeTrigger, WeightChangeTrigger, jdouble)
JNI_SETFUNC(WeightSensor, WeightChangeTrigger, WeightChangeTrigger, jdouble)
JNI_GETFUNC(WeightSensor, Weight, Weight, jdouble)
