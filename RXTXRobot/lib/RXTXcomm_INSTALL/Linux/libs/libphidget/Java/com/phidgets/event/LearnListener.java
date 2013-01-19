/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a LearnEvent. This event originates from the Phidget IR. 
 * This event occurs when a code is learned by the reader.
 * 
 * @author Phidgets Inc.
 */
public interface LearnListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void learn(LearnEvent ae);
}
