#ifndef __CPHIDGETGPS
#define __CPHIDGETGPS
#include "cphidget.h"

/** \defgroup phidgps Phidget GPS 
 * \ingroup phidgets
 * These calls are specific to the Phidget GPS object. See your device's User Guide for more specific API details, technical information, and revision details. The User Guide, along with other resources, can be found on the product page for your device.
 * @{
 */

/**
 * GPS Time in UTC.
 */
struct __GPSTime
{
	short tm_ms;	/**< Milliseconds. */
	short tm_sec;	/**< Seconds. */
	short tm_min;	/**< Minutes. */
	short tm_hour;	/**< Hours. */
} typedef GPSTime;
/**
 * GPS Date in UTC.
 */
struct __GPSDate
{
	short tm_mday;	/**< Day of the month (1-31). */
	short tm_mon;	/**< Month (1-12). */
	short tm_year;	/**< Year. */
} typedef GPSDate;

/**
 * Satellite info - used in GSV sentence.
 */
struct __GPSSatInfo
{
	short ID;
	short elevation;
	int azimuth;
	short SNR;
} typedef GPSSatInfo;

/**
 * NMEA GGA Sentence
 */
struct __GPGGA
{
	GPSTime time;
	double latitude;
	double longitude;
	short fixQuality;
	short numSatellites;
	double horizontalDilution;
	double altitude;
	double heightOfGeoid;
} typedef GPGGA;

/**
 * NMEA GSA Sentence
 */
struct __GPGSA
{
	char mode;
	/* A = auto
	 * M = forced */
	short fixType;
	/* 1 = no fix
	 * 2 = 2D
	 * 3 = 3D */
	short satUsed[12];
	/* IDs of used sats in no real order, 0 means nothing */
	double posnDilution;
	double horizDilution;
	double vertDilution;
} typedef GPGSA;

/**
 * NMEA GSV Sentence
 */
struct __GPGSV
{
	short satsInView;
	GPSSatInfo satInfo[12];
} typedef GPGSV;

/**
 * NMEA RMC Sentence
 */
struct __GPRMC
{
	GPSTime time;
	char status;
	double latitude;
	double longitude;
	double speedKnots;
	double heading;
	GPSDate date;
	double magneticVariation;
	char mode;
} typedef GPRMC;

/**
 * NMEA VTG Sentence
 */
struct __GPVTG
{
	double trueHeading;
	double magneticHeading;
	double speedKnots;
	double speed; //km/hour
	char mode;
} typedef GPVTG;

/**
 * NMEA Data Structure. Contains a set of supported NMEA sentences.
 */
struct __NMEAData
{
	GPGGA GGA;	/**< GPS Fix and position data. */
	GPGSA GSA;	/**< GPS DOP and active satellites. */
	GPGSV GSV;	/**< Detailed satellite information. */
	GPRMC RMC;	/**< Recommended minimum data. */
	GPVTG VTG;	/**< Heading and Speed over the Ground. */
} typedef NMEAData;

DPHANDLE(GPS)
CHDRSTANDARD(GPS)

/**
 * Gets the current latitude.
 * @param phid An attached phidget gps handle.
 * @param latitude The latitude.
 */
CHDRGET(GPS,Latitude,double *latitude)
/**
 * Gets the current longitude.
 * @param phid An attached phidget gps handle.
 * @param longitude The longitude.
 */
CHDRGET(GPS,Longitude,double *longitude)
/**
 * Gets the current altitude, in meters.
 * @param phid An attached phidget gps handle.
 * @param altitude The altitude.
 */
CHDRGET(GPS,Altitude,double *altitude)
/**
 * Gets the current heading, in degrees.
 * @param phid An attached phidget gps handle.
 * @param heading The heading.
 */
CHDRGET(GPS,Heading,double *heading)
/**
 * Gets the current velocity, in km/h.
 * @param phid An attached phidget gps handle.
 * @param velocity The velocity.
 */
CHDRGET(GPS,Velocity,double *velocity)
/**
 * Gets the current GPS time, in UTC.
 * @param phid An attached phidget gps handle.
 * @param time The GPS time.
 */
CHDRGET(GPS,Time,GPSTime *time)
/**
 * Gets the current GPS date, in UTC
 * @param phid An attached phidget gps handle.
 * @param date The GPS date.
 */
CHDRGET(GPS,Date,GPSDate *date)
/**
 * Gets the position fix status.
 * @param phid An attached phidget gps handle.
 * @param fixStatus The fix status.
 */
CHDRGET(GPS,PositionFixStatus,int *fixStatus)
/**
 * Gets Raw NMEA Data. This function is only available in the C API,
 * and cannot be used over the webservice.
 * The NMEA data reference points to a structure which is updated 
 * dynamically as data comes in - if you wish to work with the data
 * statically, you must make a local copy. This should be done from within
 * a position change event handler to avoid the structure changing as
 * you read it.
 * @param phid An attached phidget gps handle.
 * @param data The NMEA Data.
 */
CHDRGET(GPS,NMEAData,NMEAData *data)

/**
 * Sets a position change event handler. This is called when any of latitude, longitude, or altitude change.
 * @param phid A phidget gps handle.
 * @param fptr Callback function pointer.
 * @param userPtr A pointer for use by the user - this value is passed back into the callback function.
 */
CHDREVENT(GPS,PositionChange,double latitude,double longitude,double altitude)
/**
 * Sets a position fix status change event handler. This is called when a position fix is aquired or lost.
 * @param phid A phidget gps handle.
 * @param fptr Callback function pointer.
 * @param userPtr A pointer for use by the user - this value is passed back into the callback function.
 */
CHDREVENT(GPS,PositionFixStatusChange,int status)

#ifndef EXTERNALPROTO

struct _CPhidgetGPS {
	CPhidget phid;

	int (CCONV *fptrPositionChange)(CPhidgetGPSHandle, void *, double latitude, double longitude, double altitude);           
	void *fptrPositionChangeptr;
	int (CCONV *fptrPositionFixStatusChange)(CPhidgetGPSHandle, void *, int status);           
	void *fptrPositionFixStatusChangeptr;

	NMEAData GPSData;

	double heading, velocity, altitude, latitude, longitude;
	unsigned char fix;

	unsigned char haveTime, haveDate;

	double lastLongitude, lastLatitude, lastAltitude;
	unsigned char lastFix;

	unsigned char sckbuf[256];
	unsigned char sckbuf_write, sckbuf_read;
} typedef CPhidgetGPSInfo;
#endif

/** @} */

#endif
