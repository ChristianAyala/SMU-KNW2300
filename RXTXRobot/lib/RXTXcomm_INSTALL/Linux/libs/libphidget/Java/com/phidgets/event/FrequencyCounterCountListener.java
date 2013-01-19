/*
 * Copyright 2011 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a FrequencyCounterCountEvent. This event originates from the Phidget Frequency Counter
 * 
 * @author Phidgets Inc.
 */
public interface FrequencyCounterCountListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void frequencyCounterCounted(FrequencyCounterCountEvent ae);
}
