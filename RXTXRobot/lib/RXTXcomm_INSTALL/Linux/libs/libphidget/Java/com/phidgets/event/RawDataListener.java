/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a RawDataEvent. This event originates from the Phidget IR. 
 * This event occurs when the reader sees IR data.
 * 
 * @author Phidgets Inc.
 */
public interface RawDataListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void rawData(RawDataEvent ae);
}
