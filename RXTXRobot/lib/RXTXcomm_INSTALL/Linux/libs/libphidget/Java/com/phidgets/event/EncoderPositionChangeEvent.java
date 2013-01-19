/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

import com.phidgets.Phidget;

/**
 * This class represents the data for a EncoderPositionChangeEvent.
 * 
 * @author Phidgets Inc.
 */
public class EncoderPositionChangeEvent
{
	Phidget source;
	int index;
	int value;
	int time;

	/**
	 * Class constructor. This is called internally by the phidget library when creating this event.
	 * 
	 * @param source the Phidget object from which this event originated
	 */
	public EncoderPositionChangeEvent(Phidget source, int index, int time, int value) {
		this.source = source;
		this.index = index;
		this.value = value;
		this.time = time;
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
	 * Returns the index of the encoder.
	 * 
	 * @return the index of the encoder
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the position change of the encoder. This is the amount of change in the encoder's position
	 * since the last {@link #EncoderPositionChangeEvent}.
	 * 
	 * @return the change in position of the encoder
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns the timestamp of this position change. This is the time since the last {@link #EncoderPositionChangeEvent}.
	 * This time is not represented in a real quantitly such as seconds, but can be used as a qualitative quantity.
	 * 
	 * @return the timestamp of this change event
	 */
	public int getTime()
	{
		return time;
	}

	/**
	 * Returns a string containing information about the event.
	 * 
	 * @return an informative event string
	 */
	public String toString() {
		
		return source.toString() + " encoder position " + index + " changed by "
		  + value + " Time: " + time;
	}
}
