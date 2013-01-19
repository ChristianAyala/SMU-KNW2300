#include "../stdafx.h"
#include "phidget_jni.h"
#include "com_phidgets_AdvancedServoPhidget.h"
#include "../cphidgetadvancedservo.h"

EVENT_VARS(servoPositionChange, ServoPositionChange)
EVENT_VARS(servoVelocityChange, ServoVelocityChange)
EVENT_VARS(currentChange, CurrentChange)

JNI_LOAD(advservo, AdvancedServo)
	EVENT_VAR_SETUP(advservo, servoPositionChange, ServoPositionChange, ID, V)
	EVENT_VAR_SETUP(advservo, servoVelocityChange, ServoVelocityChange, ID, V)
	EVENT_VAR_SETUP(advservo, currentChange, CurrentChange, ID, V)
}

EVENT_HANDLER_INDEXED(AdvancedServo, servoPositionChange, ServoPositionChange, 
					  CPhidgetAdvancedServo_set_OnPositionChange_Handler, double)
EVENT_HANDLER_INDEXED(AdvancedServo, servoVelocityChange, ServoVelocityChange, 
					  CPhidgetAdvancedServo_set_OnVelocityChange_Handler, double)
EVENT_HANDLER_INDEXED(AdvancedServo, currentChange, CurrentChange, 
					  CPhidgetAdvancedServo_set_OnCurrentChange_Handler, double)

JNI_CREATE(AdvancedServo)
JNI_INDEXED_GETFUNC(AdvancedServo, Acceleration, Acceleration, jdouble)
JNI_INDEXED_SETFUNC(AdvancedServo, Acceleration, Acceleration, jdouble)
JNI_INDEXED_GETFUNC(AdvancedServo, AccelerationMin, AccelerationMin, jdouble)
JNI_INDEXED_GETFUNC(AdvancedServo, AccelerationMax, AccelerationMax, jdouble)
JNI_INDEXED_GETFUNC(AdvancedServo, VelocityLimit, VelocityLimit, jdouble)
JNI_INDEXED_SETFUNC(AdvancedServo, VelocityLimit, VelocityLimit, jdouble)
JNI_INDEXED_GETFUNC(AdvancedServo, Velocity, Velocity, jdouble)
JNI_INDEXED_GETFUNC(AdvancedServo, VelocityMin, VelocityMin, jdouble)
JNI_INDEXED_GETFUNC(AdvancedServo, VelocityMax, VelocityMax, jdouble)
JNI_INDEXED_GETFUNC(AdvancedServo, Position, Position, jdouble)
JNI_INDEXED_SETFUNC(AdvancedServo, Position, Position, jdouble)
JNI_INDEXED_GETFUNC(AdvancedServo, PositionMin, PositionMin, jdouble)
JNI_INDEXED_GETFUNC(AdvancedServo, PositionMax, PositionMax, jdouble)
JNI_INDEXED_SETFUNC(AdvancedServo, PositionMin, PositionMin, jdouble)
JNI_INDEXED_SETFUNC(AdvancedServo, PositionMax, PositionMax, jdouble)
JNI_INDEXED_GETFUNCBOOL(AdvancedServo, Engaged, Engaged)
JNI_INDEXED_SETFUNC(AdvancedServo, Engaged, Engaged, jboolean)
JNI_INDEXED_GETFUNCBOOL(AdvancedServo, Stopped, Stopped)
JNI_INDEXED_GETFUNCBOOL(AdvancedServo, SpeedRampingOn, SpeedRampingOn)
JNI_INDEXED_SETFUNC(AdvancedServo, SpeedRampingOn, SpeedRampingOn, jboolean)
JNI_INDEXED_GETFUNC(AdvancedServo, Current, Current, jdouble)
JNIEXPORT jint JNICALL
Java_com_phidgets_AdvancedServoPhidget_getServoType(JNIEnv *env, jobject obj, jint index)
{
	CPhidgetAdvancedServoHandle h = (CPhidgetAdvancedServoHandle)(uintptr_t)
	    (*env)->GetLongField(env, obj, handle_fid);
	int error;
	CPhidget_ServoType v;
	if ((error = CPhidgetAdvancedServo_getServoType(h, index, &v)))
		PH_THROW(error);
	return (jint)v;
}
JNI_INDEXED_SETFUNC(AdvancedServo, ServoType, ServoType, jint)
JNIEXPORT void JNICALL
Java_com_phidgets_AdvancedServoPhidget_setServoParameters(JNIEnv *env, jobject obj, jint index, jdouble minUs, jdouble maxUs, jdouble degrees, jdouble velocityMax)
{
	CPhidgetAdvancedServoHandle h = (CPhidgetAdvancedServoHandle)(uintptr_t)
	    (*env)->GetLongField(env, obj, handle_fid);
	int error;
	if ((error = CPhidgetAdvancedServo_setServoParameters(h, index, minUs, maxUs, degrees, velocityMax)))
		PH_THROW(error);
}
JNI_GETFUNC(AdvancedServo, MotorCount, MotorCount, jint)
