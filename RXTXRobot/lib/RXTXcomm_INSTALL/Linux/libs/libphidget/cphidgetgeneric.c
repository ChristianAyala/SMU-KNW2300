#include "stdafx.h"
#include "cphidgetgeneric.h"
#include "cusb.h"
#include "csocket.h"
#include "cthread.h"

// === Internal Functions === //

//clearVars - sets all device variables to unknown state
CPHIDGETCLEARVARS(Generic)
	ZEROMEM(phid->lastPacket, MAX_IN_PACKET_SIZE);

	return EPHIDGET_OK;
}

//initAfterOpen - sets up the initial state of an object, reading in packets from the device if needed
//				  used during attach initialization - on every attach
CPHIDGETINIT(Generic)
	TESTPTR(phid);

	//Make sure no old writes are still pending
	phid->outputPacketLen = 0;

	ZEROMEM(phid->lastPacket, MAX_IN_PACKET_SIZE);

	phid->out = phid->phid.outputReportByteLength;
	phid->in = phid->phid.inputReportByteLength;

	return EPHIDGET_OK;
}

//dataInput - parses device packets
CPHIDGETDATA(Generic)
	if (length<0) return EPHIDGET_INVALIDARG;
	TESTPTR(phid);
	TESTPTR(buffer);

	FIRE(Packet, buffer, length);
	memcpy(phid->lastPacket, buffer, length);

	return EPHIDGET_OK;
}

//eventsAfterOpen - sends out an event for all valid data, used during attach initialization
CPHIDGETINITEVENTS(Generic)
	TESTPTR(phid);
	return EPHIDGET_OK;
}

//getPacket - used by write thread to get the next packet to send to device
CGETPACKET_BUF(Generic)

//sendpacket - sends a packet to the device asynchronously, blocking if the 1-packet queue is full
CSENDPACKET_BUF(Generic)

// === Exported Functions === //

//create and initialize a device structure
CCREATE(Generic, PHIDCLASS_GENERIC)

//event setup functions
CFHANDLE(Generic, Packet,const unsigned char *, int)

CGET(Generic,INPacketLength,int)
	TESTPTRS(phid,pVal) 	
	TESTDEVICETYPE(PHIDCLASS_GENERIC)
	TESTATTACHED
	MASGN(in)
}

CGET(Generic,OUTPacketLength,int)
	TESTPTRS(phid,pVal) 	
	TESTDEVICETYPE(PHIDCLASS_GENERIC)
	TESTATTACHED
	MASGN(out)
}

int CCONV
CPhidgetGeneric_setPacket(CPhidgetGenericHandle phid, unsigned char *packet, int length)
{
	int ret = 0;

	TESTPTR(phid)
	TESTDEVICETYPE(PHIDCLASS_GENERIC)
	TESTATTACHED

	if(length!=phid->out)
		return EPHIDGET_INVALIDARG;

	CThread_mutex_lock(&phid->phid.writelock);
	ret = CPhidgetGeneric_sendpacket(phid, packet);
	CThread_mutex_unlock(&phid->phid.writelock);
	if(ret) return ret;

	return EPHIDGET_OK;
}

int CCONV
CPhidgetGeneric_getLastPacket(CPhidgetGenericHandle phid, const unsigned char **packet, int *length)
{
	TESTPTRS(phid, packet)
	TESTPTR(length)
	TESTDEVICETYPE(PHIDCLASS_GENERIC)
	TESTATTACHED

	*packet = phid->lastPacket;
	*length = phid->in;
	return EPHIDGET_OK;
}
