/*
 * Copyright 2011 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a CurrentChangeEvent. This event originates from the Phidget Motor Controller.
 * This event is not supported by all Motor Controllers.
 * 
 * @author Phidgets Inc.
 */
public interface CurrentChangeListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void currentChanged(CurrentChangeEvent ae);
}
