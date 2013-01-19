/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a AccelerationChangeEvent. This event originates from the Phidget Accelerometer
 * 
 * @author Phidgets Inc.
 */
public interface AccelerationChangeListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void accelerationChanged(AccelerationChangeEvent ae);
}
