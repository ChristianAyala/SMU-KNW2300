/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

/**
 * This interface represents a ErrorEvent. This event originates from all Phidgets.
 * It is used for asynchronous error handling.
 * 
 * @author Phidgets Inc.
 */
public interface ErrorListener
{
	/**
	 * This method is called with the event data when a new event arrives.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void error(ErrorEvent ae);
}
