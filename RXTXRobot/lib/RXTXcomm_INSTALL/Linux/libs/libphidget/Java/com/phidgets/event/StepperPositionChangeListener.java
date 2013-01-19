/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a StepperPositionChangeEvent. This event originates from the Phidget Stepper Controller and the 
 * Phidget Advanced Stepper Controller.
 * 
 * @author Phidgets Inc.
 */
public interface StepperPositionChangeListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void stepperPositionChanged(StepperPositionChangeEvent ae);
}
