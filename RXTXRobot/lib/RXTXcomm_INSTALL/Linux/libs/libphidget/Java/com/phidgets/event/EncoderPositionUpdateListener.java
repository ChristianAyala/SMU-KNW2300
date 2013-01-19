/*
 * Copyright 2011 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents an EncoderPositionUpdateEvent. This event originates from the Phidget Motor Controller. This event is not supported by all Motor Controllers.
 * 
 * @author Phidgets Inc.
 */
public interface EncoderPositionUpdateListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void encoderPositionUpdated(EncoderPositionUpdateEvent ae);
}
