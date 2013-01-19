#ifndef __CPHIDGETANALOG
#define __CPHIDGETANALOG
#include "cphidget.h"

/** \defgroup phidanalog Phidget Analog 
 * \ingroup phidgets
 * These calls are specific to the Phidget Analog object. See your device's User Guide for more specific API details, technical information, and revision details. The User Guide, along with other resources, can be found on the product page for your device.
 * @{
 */

DPHANDLE(Analog)
CHDRSTANDARD(Analog)

/**
 * Gets the number of outputs supported by this phidget analog.
 * @param phid An attached phidget analog handle.
 * @param count The axis count.
 */
CHDRGET(Analog,OutputCount,int *count)
/**
 * Gets the currently set voltage for an output, in V.
 * @param phid An attached phidget analog handle.
 * @param index The output index.
 * @param voltage The voltage.
 */
CHDRGETINDEX(Analog,Voltage,double *voltage)
/**
 * Sets the voltage of an output, in V.
 * @param phid An attached phidget analog handle.
 * @param index The otuput index.
 * @param voltage The output voltage.
 */
CHDRSETINDEX(Analog,Voltage,double voltage)
/**
 * Gets the maximum settable output voltage, in V.
 * @param phid An attached phidget analog handle.
 * @param index The output index.
 * @param max The max voltage.
 */
CHDRGETINDEX(Analog,VoltageMax,double *max)
/**
 * Gets the minimum settable output voltage, in V.
 * @param phid An attached phidget analog handle.
 * @param index The output index.
 * @param min The min voltage.
 */
CHDRGETINDEX(Analog,VoltageMin,double *min)
/**
 * Sets the enabled state for an output.
 * @param phid An attached phidget analog handle.
 * @param index The output index.
 * @param enabledState The enabled state. Possible values are \ref PTRUE and \ref PFALSE.
 */
CHDRSETINDEX(Analog,Enabled,int enabledState)
/**
 * Gets the enabled state for an output.
 * @param phid An attached phidget analog handle.
 * @param index The output index.
 * @param enabledState The enabled state. Possible values are \ref PTRUE and \ref PFALSE.
 */
CHDRGETINDEX(Analog,Enabled,int *enabledState)
	
#ifndef EXTERNALPROTO

#define ANALOG_MAXOUTPUTS 4

struct _CPhidgetAnalog 
{
	CPhidget phid;

	double voltage[ANALOG_MAXOUTPUTS];
	unsigned char enabled[ANALOG_MAXOUTPUTS];

	double voltageEcho[ANALOG_MAXOUTPUTS];
	unsigned char enabledEcho[ANALOG_MAXOUTPUTS];
	
	double voltageMax, voltageMin;
	
	double nextVoltage[ANALOG_MAXOUTPUTS];
	double lastVoltage[ANALOG_MAXOUTPUTS];
	unsigned char changedVoltage[ANALOG_MAXOUTPUTS];
	
	unsigned char nextEnabled[ANALOG_MAXOUTPUTS];
	unsigned char lastEnabled[ANALOG_MAXOUTPUTS];
	unsigned char changedEnabled[ANALOG_MAXOUTPUTS];

	unsigned char lastOvercurrent[ANALOG_MAXOUTPUTS];
	unsigned char lastTsd;

	unsigned char controlPacketWaiting;
	unsigned char lastOutputPacket;
} typedef CPhidgetAnalogInfo;

#endif

/** @} */
#endif
