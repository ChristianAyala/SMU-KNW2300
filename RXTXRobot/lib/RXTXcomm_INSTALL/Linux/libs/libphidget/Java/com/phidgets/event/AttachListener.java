/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a AttachEvent. This event originates from all Phidgets.
 * 
 * @author Phidgets Inc.
 */
public interface AttachListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void attached(AttachEvent ae);
}
