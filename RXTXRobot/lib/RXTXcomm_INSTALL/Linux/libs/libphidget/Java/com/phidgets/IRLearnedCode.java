
/*
 * Copyright 2012 Phidgets Inc.  All rights reserved.
 */

package com.phidgets;
/**
 * This class represents an IR Learned Code. This is used in the Learn event.
 *
 * @author Phidget Inc.
 */
public final class IRLearnedCode
{
	private IRCode code;
	private IRCodeInfo codeInfo;
	
	/**
	* Creates a new IR Learned Code. This is used in the Learn event.
	* @param code the IR code
	* @param codeInfo the code parameters
	*/
	public IRLearnedCode(IRCode code, IRCodeInfo codeInfo)
	{
		this.code=code;
		this.codeInfo=codeInfo;
	}
	
	/**
	 * Returns the code.
	 * @return code
	 */
	public IRCode getCode()
	{
		return code;
	}

	/**
	 * Returns the code parameters.
	 * @return code parameters
	 */
	public IRCodeInfo getCodeInfo()
	{
		return codeInfo;
	}
	
	public String toString()
	{
		return code.toString() + " (" + code.getBitCount() + "-bit)\n" + codeInfo.toString();
	}
}

