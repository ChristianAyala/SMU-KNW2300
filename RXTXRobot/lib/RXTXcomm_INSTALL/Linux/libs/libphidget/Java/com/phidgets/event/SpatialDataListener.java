/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a SpatialDataEvent. This event originates from the Phidget Spatial. 
 * This event occurs when spatial data comes in
 * 
 * @author Phidgets Inc.
 */
public interface SpatialDataListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void data(SpatialDataEvent ae);
}
