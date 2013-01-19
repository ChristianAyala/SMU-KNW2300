/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a OutputChangeEvent. This event originates from the 
 * Phidget Interface Kit and the Phidget RFID Reader
 * 
 * @author Phidgets Inc.
 */
public interface OutputChangeListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void outputChanged(OutputChangeEvent ae);
}
