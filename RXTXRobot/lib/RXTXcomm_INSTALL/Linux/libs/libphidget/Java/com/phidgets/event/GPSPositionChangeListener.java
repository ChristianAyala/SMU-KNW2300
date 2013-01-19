/*
 * Copyright 2011 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a GPSPositionChangeEvent. This event originates from the Phidget GPS
 * 
 * @author Phidgets Inc.
 */
public interface GPSPositionChangeListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void gpsPositionChanged(GPSPositionChangeEvent ae);
}
