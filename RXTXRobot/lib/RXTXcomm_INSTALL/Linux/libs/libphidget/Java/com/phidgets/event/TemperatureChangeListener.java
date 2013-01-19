/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a TemperatureChangeEvent. This event originates from the Phidget Temperature Sensor
 * 
 * @author Phidgets Inc.
 */
public interface TemperatureChangeListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void temperatureChanged(TemperatureChangeEvent ae);
}
