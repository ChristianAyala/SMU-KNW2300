/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a ServoPositionChangeEvent. This event originates from the Phidget Servo Controller and the 
 * Phidget Advanced Servo Controller.
 * 
 * @author Phidgets Inc.
 */
public interface ServoPositionChangeListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void servoPositionChanged(ServoPositionChangeEvent ae);
}
