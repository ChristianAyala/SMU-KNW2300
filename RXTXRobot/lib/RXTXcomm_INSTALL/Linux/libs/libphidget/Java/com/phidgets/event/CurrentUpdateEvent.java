/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

import com.phidgets.Phidget;

/**
 * This class represents the data for a CurrentUpdateEvent.
 * 
 * @author Phidgets Inc.
 */
public class CurrentUpdateEvent
{
	Phidget source;
	int index;
	double value;

	/**
	 * Class constructor. This is called internally by the Phidget library when creating this event.
	 * 
	 * @param source the Phidget object from which this event originated
	 */
	public CurrentUpdateEvent(Phidget source, int index, double value)
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
	 * Returns the index of the motor.
	 * 
	 * @return the index of the motor
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the current of the motor. This is a representation of the amount of current being used by the motor.
	 * 
	 * @return the motor's current draw
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Returns a string containing information about the event.
	 * 
	 * @return an informative event string
	 */
	public String toString() {
		return source.toString() + " current " + index + " is "
		  + value;
	}
}
