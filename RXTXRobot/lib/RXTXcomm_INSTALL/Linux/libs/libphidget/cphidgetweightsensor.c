#include "stdafx.h"
#include "cphidgetweightsensor.h"
#include "cusb.h"
#include "csocket.h"
#include "cthread.h"
#include <math.h>

// === Internal Functions === //

//clearVars - sets all device variables to unknown state
CPHIDGETCLEARVARS(WeightSensor)

	phid->Weight = PUNI_DBL;
	phid->lastweight = PUNK_DBL;
	phid->WeightChangeTrigger = PUNI_DBL;

	return EPHIDGET_OK;
}

//initAfterOpen - sets up the initial state of an object, reading in packets from the device if needed
//				  used during attach initialization - on every attach
CPHIDGETINIT(WeightSensor)
	TESTPTR(phid);

	//initialize triggers, set data arrays to unknown
	phid->Weight = PUNK_DBL;
	phid->lastweight = PUNK_DBL;
	phid->WeightChangeTrigger = 0.1;

	//issue one read
	CPhidget_read((CPhidgetHandle)phid);

	return EPHIDGET_OK;
}

//dataInput - parses device packets
CPHIDGETDATA(WeightSensor)
	double Weight = 0;

	if (length<0) return EPHIDGET_INVALIDARG;
	TESTPTR(phid);
	TESTPTR(buffer);
	
	//Parse device packets - store data locally
	switch(phid->phid.deviceIDSpec)
	{
		case PHIDID_WEIGHTSENSOR:
			Weight = (((double)((unsigned short)buffer[0]+((unsigned short)buffer[1]<<8))) / 100.0 ) - 40.0;
			Weight = round_double(Weight, 2);
			break;
		default:
			return EPHIDGET_UNEXPECTED;
	}

	//store to structure
	phid->Weight = Weight;

	//send out any events that exceed or match the trigger
	if (fabs(phid->Weight - phid->lastweight) >= phid->WeightChangeTrigger || phid->lastweight == PUNK_DBL)
	{
		FIRE(WeightChange, phid->Weight);
		phid->lastweight = phid->Weight;
	}
	return EPHIDGET_OK;
}

//eventsAfterOpen - sends out an event for all valid data, used during attach initialization
CPHIDGETINITEVENTS(WeightSensor)
	if (phid->Weight != PUNK_DBL)
	{
		FIRE(WeightChange, phid->Weight);
		phid->lastweight = phid->Weight;
	}
	return EPHIDGET_OK;
}

//getPacket - not used for WeightSensor
CGETPACKET(WeightSensor)
	return EPHIDGET_UNEXPECTED;
}

// === Exported Functions === //

//create and initialize a device structure
CCREATE(WeightSensor, PHIDCLASS_WEIGHTSENSOR)

//event setup functions
CFHANDLE(WeightSensor, WeightChange, double)

CGET(WeightSensor,Weight,double)
	TESTPTRS(phid,pVal) 
	TESTDEVICETYPE(PHIDCLASS_WEIGHTSENSOR)
	TESTATTACHED
	TESTMASGN(Weight, PUNK_DBL)
	MASGN(Weight)
}

CGET(WeightSensor,WeightChangeTrigger,double)
	TESTPTRS(phid,pVal) 
	TESTDEVICETYPE(PHIDCLASS_WEIGHTSENSOR)
	TESTATTACHED
	TESTMASGN(WeightChangeTrigger, PUNK_DBL)
	MASGN(WeightChangeTrigger)
}
CSET(WeightSensor,WeightChangeTrigger,double)
	TESTPTR(phid) 
	TESTDEVICETYPE(PHIDCLASS_WEIGHTSENSOR)
	TESTATTACHED
	TESTRANGE(0, 300)
	if(CPhidget_statusFlagIsSet(phid->phid.status, PHIDGET_REMOTE_FLAG))
		ADDNETWORKKEY(Trigger, "%lE", WeightChangeTrigger);
	else
		phid->WeightChangeTrigger = newVal;
	return EPHIDGET_OK;
}

