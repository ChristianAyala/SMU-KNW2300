#ifndef __CPHIDGETGENERIC
#define __CPHIDGETGENERIC
#include "cphidget.h"

DPHANDLE(Generic)
CHDRSTANDARD(Generic)

CHDRGET(Generic,INPacketLength,int *length)
CHDRGET(Generic,OUTPacketLength,int *length)

CHDRGET(Generic,LastPacket,const unsigned char **packet, int *length)
CHDRSET(Generic,Packet,unsigned char *packet, int length)
CHDREVENT(Generic,Packet,const unsigned char *packet, int length)

#ifndef EXTERNALPROTO
struct _CPhidgetGeneric {
	CPhidget phid;

	int (CCONV *fptrPacket)(CPhidgetGenericHandle, void *, const unsigned char *, int);           
	void *fptrPacketptr;

	unsigned char lastPacket[MAX_IN_PACKET_SIZE];

	unsigned char buffer[MAX_OUT_PACKET_SIZE];

	int in, out;

	unsigned char outputPacket[MAX_OUT_PACKET_SIZE];
	unsigned int outputPacketLen;
} typedef CPhidgetGenericInfo;
#endif

/** @} */

#endif
