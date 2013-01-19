/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

import com.phidgets.Phidget;
import com.phidgets.SpatialEventData;

/**
 * This class represents the data for a SpatialDataEvent.
 * 
 * @author Phidgets Inc.
 */
public class SpatialDataEvent
{
	Phidget source;
	SpatialEventData[] data;

	/**
	 * Class constructor. This is called internally by the phidget library when creating this event.
	 * 
	 * @param source the Phidget object from which this event originated
	 * @param data the spatial data
	 */
	public SpatialDataEvent(Phidget source, SpatialEventData[] data)
	{
		this.source = source;
		this.data = data;
	}

	/**
	 * Returns the source Phidget of this event. This is a reference to the Phidget object from which this
	 * event was called. This object can be cast into a specific type of Phidget object to call specific
	 * device calls on it.
	 * 
	 * @return the event caller
	 */
	public Phidget getSource() {
		return source;
	}

	/**
	 * Returns the data. This may contain multiple sets of data for high data rates.
	 * 
	 * @return the data
	 */
	public SpatialEventData[] getData() {
		return data;
	}

	/**
	 * Returns a string containing information about the event.
	 * 
	 * @return an informative event string
	 */
	public String toString() {
		return source.toString() + " Spatial Data";
	}
}
