/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a StepperVelocityChangeEvent. This event originates from the Phidget Stepper Controller
 * 
 * @author Phidgets Inc.
 */
public interface StepperVelocityChangeListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void stepperVelocityChanged(StepperVelocityChangeEvent ae);
}
