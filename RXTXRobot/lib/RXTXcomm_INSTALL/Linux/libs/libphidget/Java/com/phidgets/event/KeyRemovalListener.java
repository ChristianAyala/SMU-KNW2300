/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a KeyRemovalEvent. This event originates from the Phidget Dictionary. 
 * This event occurs key that matches the listen pattern is removed.
 * 
 * @author Phidgets Inc.
 */
public interface KeyRemovalListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void keyRemoved(KeyRemovalEvent ae);
}
