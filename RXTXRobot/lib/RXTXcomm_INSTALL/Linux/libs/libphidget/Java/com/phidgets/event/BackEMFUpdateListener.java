/*
 * Copyright 2011 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a BackEMFUpdateEvent. This event originates from the 
 * Phidget Motor Control. This event is not supported by all Motor Controllers.
 * 
 * @author Phidgets Inc.
 */
public interface BackEMFUpdateListener
{
	/**
	 * This method is called with the event data every 16ms, when back EMF sensing is enabled for that motor.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void backEMFUpdated(BackEMFUpdateEvent ae);
}
