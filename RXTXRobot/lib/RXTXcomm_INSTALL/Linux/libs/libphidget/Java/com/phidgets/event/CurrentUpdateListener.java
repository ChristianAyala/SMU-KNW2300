/*
 * Copyright 2011 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a CurrentUpdateEvent. This event originates from the Phidget Motor Controller. This event is not supported by all Motor Controllers.
 * 
 * @author Phidgets Inc.
 */
public interface CurrentUpdateListener
{
	/**
	 * This method is called with every 8ms.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void currentUpdated(CurrentUpdateEvent ae);
}
