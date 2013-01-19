/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a TagLossEvent. This event originates from the Phidget RFID reader. Tag loss events
 * occur when a tag is removed from the RFID reader.
 * 
 * @author Phidgets Inc.
 */
public interface TagLossListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void tagLost(TagLossEvent ae);
}
