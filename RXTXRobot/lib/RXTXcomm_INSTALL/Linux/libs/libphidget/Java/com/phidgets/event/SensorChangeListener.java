/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;
/**
 * This interface represents a SensorChangeEvent. This event originates from the Phidget Interface Kit
 * 
 * @author Phidgets Inc.
 */
public interface SensorChangeListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void sensorChanged(SensorChangeEvent ae);
}
