/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a ServoVelocityChangeEvent. This event originates from the Phidget Advanced Servo Controller
 * 
 * @author Phidgets Inc.
 */
public interface ServoVelocityChangeListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void servoVelocityChanged(ServoVelocityChangeEvent ae);
}
