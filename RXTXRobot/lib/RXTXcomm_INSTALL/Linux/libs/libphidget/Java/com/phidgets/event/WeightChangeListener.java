/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a WeightChangeEvent. This event originates from the Phidget Weight Sensor
 * 
 * @author Phidgets Inc.
 */
public interface WeightChangeListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void weightChanged(WeightChangeEvent ae);
}
