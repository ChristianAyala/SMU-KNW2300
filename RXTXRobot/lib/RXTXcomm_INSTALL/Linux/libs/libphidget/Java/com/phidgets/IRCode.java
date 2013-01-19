
/*
 * Copyright 2012 Phidgets Inc.  All rights reserved.
 */

package com.phidgets;
/**
 * This class represents an IR Code.
 *
 * @author Phidget Inc.
 */
public final class IRCode
{
	private short[] data;
	/**
	* IR code data. This is MSB first, right justified. This is really an (unsigned) Byte array, 
	* so values range from 0-255. We have to use shorts because Java doesn't support unsigned types.
	*/
	public short[] getData()
	{
		return data;
	}
	private int bitCount;
	/**
	* Data bits. This is important because many codes use a number of bits that doesn't line up with byte (8-bit) borders.
	*/
	public int getBitCount()
	{
		return bitCount;
	}

	/**
	* Creates a new IR Code from a string.
	* @param code the IR code
	* @param bitCount the code length in bits.
	*/
	public IRCode(String code, int bitCount)
	{ 
		this.data = HexToData(code);
		this.bitCount = bitCount;
	}

	/**
	* Creates a new IR Code from a data array.
	* @param data the IR code data
	* @param bitCount the code length in bits.
	*/
	public IRCode(short[] data, int bitCount)
	{ 
		int length = (bitCount / 8) + ((bitCount % 8 > 0) ? 1 : 0);

		this.data = new short[length];
		for (int i = 0; i < length; i++)
			this.data[i] = data[i];

		this.bitCount = bitCount;
	}

	private short[] HexToData(String hexString)
	{
		if (hexString == null)
			return null;

		if (hexString.startsWith("0x")){
			hexString = hexString.substring(2);
		}
	
		if (hexString.length() % 2 == 1)
			hexString = '0' + hexString; // Up to you whether to pad the first or last byte

		short[] data = new short[hexString.length() / 2];

		for (int i = 0; i < data.length; i++){
			data[i] = (short) Integer.parseInt(hexString.substring(i * 2, (i * 2) + 2), 16);
		}

		return data;
	}
    private char[] hexlookup = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	/**
	* String representation of the IR code.
	*/
	public String toString()
	{
		String out = "";
		for(int i=0;i<data.length;i++)
		{
			out = out + (hexlookup[data[i] / 16]);
			out = out + (hexlookup[data[i] % 16]);
		}
		return out;
	}
}
