/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

import com.phidgets.Phidget;
import com.phidgets.PhidgetException;

/**
 * This class represents the data for a ErrorEvent.
 * 
 * @author Phidgets Inc.
 */
public class ErrorEvent
{
	Phidget source;
	PhidgetException exception;

	/**
	 * Class constructor. This is called internally by the phidget library when creating this event.
	 * 
	 * @param source the Phidget object from which this event originated
	 */
	public ErrorEvent(Phidget source, PhidgetException ex) {
		this.source = source;
		this.exception = ex;
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
	 * Returns the exception that describes the error.
	 * 
	 * @return the event exception
	 */
	public PhidgetException getException()
	{
		return exception;
	}

	/**
	 * Returns a string containing information about the event.
	 * 
	 * @return an informative event string
	 */
	public String toString() {
		return "Error Event (" + exception.getErrorNumber() + "): " + exception.getDescription();
	}
}
