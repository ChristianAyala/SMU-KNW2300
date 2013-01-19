/*
 * Copyright 2011 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

import com.phidgets.Phidget;

/**
 * This class represents the data for a SensorUpdateEvent.
 * 
 * @author Phidgets Inc.
 */
public class SensorUpdateEvent
{
	Phidget source;
	int index;
	int value;

	/**
	 * Class constructor. This is called internally by the phidget library when creating this event.
	 * 
	 * @param source the Phidget object from which this event originated
	 */
	public SensorUpdateEvent(Phidget source, int index, int value)
	{
		this.source = source;
		this.index = index;
		this.value = value;
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
	 * Returns the index of the sensor.
	 * 
	 * @return the index of the sensor
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Retuns the value of the sensor. This value ranges from 0-1000.
	 * 
	 * @return the value of the sensor
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns a string containing information about the event.
	 * 
	 * @return an informative event string
	 */
	public String toString() {
		return source.toString() + " sensor " + index + " is "
		  + value;
	}
}
