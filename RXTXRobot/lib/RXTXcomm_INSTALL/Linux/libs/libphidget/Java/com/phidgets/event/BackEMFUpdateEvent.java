/*
 * Copyright 2011 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

import com.phidgets.Phidget;

/**
 * This class represents the data for a BackEMFUpdateEvent.
 * 
 * @author Phidgets Inc.
 */
public class BackEMFUpdateEvent
{
	Phidget source;
	int index;
	double voltage;

	/**
	 * Class constructor. This is called internally by the phidget library when creating this event.
	 * 
	 * @param source the Phidget object from which this event originated
	 */
	public BackEMFUpdateEvent(Phidget source, int index, double voltage)
	{
		this.source = source;
		this.index = index;
		this.voltage = voltage;
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
	 * Returns the backEMF value.
	 * 
	 * @return the backEMF value
	 */
	public double getVoltage() {
		return voltage;
	}

	/**
	 * Returns a string containing information about the event.
	 * 
	 * @return an informative event string
	 */
	public String toString() {
		return source.toString() + " BackEMF Value " + index + " is "
		  + voltage;
	}
}
