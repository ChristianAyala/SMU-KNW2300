/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

import com.phidgets.Phidget;

/**
 * This class represents the data for a GPSPositionFixStatusChangeEvent.
 * 
 * @author Phidgets Inc.
 */   
public class GPSPositionFixStatusChangeEvent
{
	Phidget source;
	boolean status;


	/**
	 * Class constructor. This is called internally by the phidget library when creating this event.
	 * 
	 * @param source the Phidget object from which this event originated
	 */
	public GPSPositionFixStatusChangeEvent(Phidget source, boolean status) {
		this.source = source;
		this.status = status;
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
	 * Returns the position fix status of the GPS. 
	 * 
	 * @return the position fix status
	 */
	public boolean getStatus() {
		return status;
	}

	/**
	 * Returns a string containing information about the event.
	 * 
	 * @return an informative event string
	 */
	public String toString() {
		return("Position fix status IS : " + status);
	}
}
