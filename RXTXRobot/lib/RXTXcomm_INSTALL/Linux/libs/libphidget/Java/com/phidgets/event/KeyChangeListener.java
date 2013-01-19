/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a KeyChangeEvent. This event originates from the Phidget Dictionary. Key Change events
 * occur when key that matches the listen pattern is either added or changes in the Dictionary.
 * 
 * @author Phidgets Inc.
 */
public interface KeyChangeListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void keyChanged(KeyChangeEvent ae);
}
