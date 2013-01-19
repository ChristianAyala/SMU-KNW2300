/*
 * Copyright 2011 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;
/**
 * This interface represents a BridgeDataEvent. This event originates from the Phidget Bridge
 * 
 * @author Phidgets Inc.
 */
public interface BridgeDataListener
{
	/**
	 * This method is called with the event data when a new event arrives. The event is issued at the specified data rate, for 
	 * each enabled bridge.
	 * 
	 * @param ae the event data object containing event data
	 */
	public void bridgeData(BridgeDataEvent ae);
}
