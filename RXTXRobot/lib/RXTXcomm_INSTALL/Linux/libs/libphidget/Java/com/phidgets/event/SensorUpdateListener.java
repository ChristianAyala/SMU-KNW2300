/*
 * Copyright 2011 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;
/**
 * This interface represents a SensorUpdateEvent. This event originates from the Phidget Motor Control
 * 
 * @author Phidgets Inc.
 */
public interface SensorUpdateListener
{
	/**
	 * This method is called with the event data every 8ms.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void sensorUpdated(SensorUpdateEvent ae);
}
