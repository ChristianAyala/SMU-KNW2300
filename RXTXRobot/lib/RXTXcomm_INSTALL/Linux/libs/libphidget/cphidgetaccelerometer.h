#ifndef __CPHIDGETACCELEROMETER
#define __CPHIDGETACCELEROMETER
#include "cphidget.h"

/** \defgroup phidaccel Phidget Accelerometer 
 * \ingroup phidgets
 * These calls are specific to the Phidget Accelerometer object. See your device's User Guide for more specific API details, technical information, and revision details. The User Guide, along with other resources, can be found on the product page for your device.
 * @{
 */

DPHANDLE(Accelerometer)
CHDRSTANDARD(Accelerometer)

/**
 * Gets the number of acceleration axes supported by this accelerometer.
 * @param phid An attached phidget accelerometer handle.
 * @param count The axis count.
 */
CHDRGET(Accelerometer,AxisCount,int *count)

/**
 * Gets the current acceleration of an axis.
 * @param phid An attached phidget accelerometer handle.
 * @param index The acceleration index.
 * @param acceleration The acceleration.
 */
CHDRGETINDEX(Accelerometer,Acceleration,double *acceleration)
/**
 * Gets the maximum accleration supported by an axis.
 * @param phid An attached phidget accelerometer handle.
 * @param index The acceleration index
 * @param max The maximum acceleration
 */
CHDRGETINDEX(Accelerometer,AccelerationMax,double *max)
/**
 * Gets the minimum acceleraiton supported by an axis.
 * @param phid An attached phidget accelerometer handle.
 * @param index The acceleration index
 * @param min The minimum acceleraion
 */
CHDRGETINDEX(Accelerometer,AccelerationMin,double *min)
/**
 * Sets an acceleration change event handler. This is called when the acceleration changes by more then the change trigger.
 * @param phid A phidget accelerometer handle.
 * @param fptr Callback function pointer.
 * @param userPtr A pointer for use by the user - this value is passed back into the callback function.
 */
CHDREVENTINDEX(Accelerometer,AccelerationChange,double acceleration)
/**
 * Gets the change trigger for an axis.
 * @param phid An attached phidget accelerometer handle.
 * @param index The acceleration index
 * @param trigger The change trigger.
 */
CHDRGETINDEX(Accelerometer,AccelerationChangeTrigger,double *trigger)
/**
 * Sets the change trigger for an axis.
 * @param phid An attached phidget accelerometer handle.
 * @param index The acceleration index
 * @param trigger The change trigger.
 */
CHDRSETINDEX(Accelerometer,AccelerationChangeTrigger,double trigger)

#ifndef REMOVE_DEPRECATED
DEP_CHDRGET("Deprecated - use CPhidgetAccelerometer_getAxisCount",Accelerometer,NumAxis,int *)
#endif

#ifndef EXTERNALPROTO
#define ACCEL_MAXAXES 3
struct _CPhidgetAccelerometer {
	CPhidget phid;
	int (CCONV *fptrAccelerationChange)(CPhidgetAccelerometerHandle, void *, int, double);           
	void *fptrAccelerationChangeptr;

	double axis[ACCEL_MAXAXES];
	double axisChangeTrigger[ACCEL_MAXAXES];
	double axisLastTrigger[ACCEL_MAXAXES];

	double accelerationMax, accelerationMin;
} typedef CPhidgetAccelerometerInfo;
#endif

/** @} */

#endif
