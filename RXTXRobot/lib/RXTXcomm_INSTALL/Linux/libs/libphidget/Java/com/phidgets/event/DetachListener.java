/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a DetachEvent. This event originates from all Phidgets, as well as the Phidget Manager.
 * 
 * @author Phidgets Inc.
 */
public interface DetachListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void detached(DetachEvent ae);
}
