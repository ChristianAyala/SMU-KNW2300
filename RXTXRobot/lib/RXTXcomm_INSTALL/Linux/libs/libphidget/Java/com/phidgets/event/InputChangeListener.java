/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a InputChangeEvent. This event originates from the Phidget Encoder, the Phidget InterfaceKit,
 * and the Phidget Motor Controller.
 * 
 * @author Phidgets Inc.
 */
public interface InputChangeListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void inputChanged(InputChangeEvent ae);
}
