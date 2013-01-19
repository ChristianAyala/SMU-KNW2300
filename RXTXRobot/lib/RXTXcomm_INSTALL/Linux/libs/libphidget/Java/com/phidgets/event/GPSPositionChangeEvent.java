/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

import com.phidgets.Phidget;

/**
 * This class represents the data for a GPSPositionChangeEvent.
 * 
 * @author Phidgets Inc.
 */   
public class GPSPositionChangeEvent
{
	Phidget source;
	double latitude;
	double longitude;
	double altitude;

	/**
	 * Class constructor. This is called internally by the phidget library when creating this event.
	 * 
	 * @param source the Phidget object from which this event originated
	 */
	public GPSPositionChangeEvent(Phidget source, double latitude,	double longitude, double altitude) {
		this.source = source;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	/**
	 * Returns the source Phidget of this event. This is a reference to the Phidget object from which this
	 * event was called. This object can be cast into a specific type of Phidget object to call specific
	 * device calls on it.
	 * 
	 * @return the event caller
	 */
	public Phidget getSource() {
		return source;
	}

	/**
	 * Returns the latitude of the GPS, in signed degrees format.
	 * 
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Returns the longitude of the GPS, in signed degrees format
	 * 
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Returns the altitude of the GPS, in meters.
	 * 
	 * @return the altitude
	 */
	public double getAltitude() {
		return altitude;
	}
	
	/**
	 * Returns a string containing information about the event.
	 * 
	 * @return an informative event string
	 */
	public String toString() {
		return("Position is - Latitude: "+ latitude + " degrees" +
			", Longitude: " + longitude + " degrees" + ", Altitude: "+ altitude + "m");
	}
}
