/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a TagGainEvent. This event originates from the Phidget RFID Reader. 
 * This event occurs when a tag is placed on a reader.
 * 
 * @author Phidgets Inc.
 */
public interface TagGainListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void tagGained(TagGainEvent ae);
}
