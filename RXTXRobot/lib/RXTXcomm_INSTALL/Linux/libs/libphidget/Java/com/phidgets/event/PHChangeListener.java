/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a PHChangeEvent. This event originates from the Phidget PH Sensor
 * 
 * @author Phidgets Inc.
 */
public interface PHChangeListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void phChanged(PHChangeEvent ae);
}
