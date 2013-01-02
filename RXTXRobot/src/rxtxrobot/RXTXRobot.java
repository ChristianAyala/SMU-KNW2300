package rxtxrobot;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author Chris King
 * @version 3.0.0
 */

public class RXTXRobot
{
	/* Constants - These should not change unless you know what you are doing */
	final private static String API_VERSION = "3.0.0";
	final private static boolean ONLY_ALLOW_TWO_MOTORS = true;
	final private static int BAUD_RATE = 9600;
	/**
	 * Refers to the servo motor located in SERVO1
	 */
	final public static int SERVO1 = 1;
	/**
	 * Refers to the servo motor located in SERVO2
	 */
	final public static int SERVO2 = 0;
	/**
	 * Refers to the M1 DC MOTOR
	 */
	final public static int MOTOR1 = 0;
	/**
	 * Refers to the M2 DC MOTOR
	 */
	final public static int MOTOR2 = 1;
	/**
	 * Refers to the M3 DC Motor
	 */
	final public static int MOTOR3 = 2;
	/**
	 * Refers to the M4 DC Motor
	 */
	final public static int MOTOR4 = 3;
	
	/**
	 * The maximum number of digital pins that can be read
	 * from the Arduino (0 &lt; pins &lt; NUM_DIGITAL_PINS)
	 */
	final public static int NUM_DIGITAL_PINS = 1;
	/**
	 * The maximum number of analog pins that can be read
	 * from the Arduino (0 &lt; pins &lt; NUM_ANALOG_PINS)
	 */
	final public static int NUM_ANALOG_PINS = 6;

	/* Private variables */
	private String port;
	private boolean verbose;
	private int[] analogPinCache;
	private int[] digitalPinCache;
	private boolean[] motorsRunning = {false, false, false, false};

	private InputStream in;
	private OutputStream out;
	private SerialPort sPort;
	private CommPort cPort;

	/**
	 * Creates a new RXTXRobot object.
	 *
	 * This constructor creates a new RXTXRobot object.
	 */
	public RXTXRobot()
	{
		port = "";
		verbose = false;
		analogPinCache = null;
		digitalPinCache = null;
	}

	/**
	 * Gets the version number of the API.
	 *
	 * @return A string with the version number
	 */
	public static String getVersion()
	{
		return API_VERSION;
	}

	/**
	 * Sets the output PrintStream; System.out by default.
	 *
	 * This method lets you set the output stream from System.out
	 * to somewhere else.  It shouldn't be used unless you know
	 * what you are doing.
	 *
	 * @param p PrintStream to write output to.
	 */
	public void setOutStream(PrintStream p)
	{
		System.setOut(p);
	}
	
	/**
	 * Sets the error PrintStream; System.err by default.
	 *
	 * This method lets you set the error stream from System.err
	 * to somewhere else.  It shouldn't be used unless you know
	 * what you are doing.
	 *
	 * @param p PrintStream to write error to.
	 */
	public void setErrStream(PrintStream p)
	{
		System.setErr(p);
	}

	/**
	 * Sets verbose output.
	 *
	 * Setting verbose output allows for easier debugging and
	 * seeing what is happening behind the scenes.  Turn this
	 * on to debug your code.
	 *
	 * @param v Boolean to set verbosity.
	 */
	public void setVerbose(boolean v)
	{
		this.verbose = v;
	}

	/**
	 * Sets the port to connect to the Arduino or XBee.
	 *
	 * Set the port to the same port that the Arduino or XBee
	 * is connected to on your computer.
	 * For example:
	 * 	On Windows, "COM3" (check device manager)
	 * 	On Mac, "/dev/tty.usbmodem411" (run "ls /dev | grep usb")
	 * 	On Linux, "/dev/ttyACM0"
	 *
	 * @param p Port name that the Arduino/XBee is connected to.
	 */
	public void setPort(String p)
	{
		this.port = p;
	}

	/**
	 * Gets the port that the RXTXRobot is using.
	 *
	 * @return The port the RXTXRobot is using.
	 */
	public String getPort()
	{
		return this.port;
	}

	/**
	 * Prints out debugging statements.
	 *
	 * If verbose is set to true, then debugging statements will
	 * be printed out to the user.  If verbose is set to false,
	 * then these statements will not print.
	 *
	 * @param str Message passed to debug
	 */
	private void debug(String str)
	{
		if (verbose)
			System.out.println("--> " + str);
	}

	/**
	 * Allows the robot to sleep for a time length (measured in milliseconds).
	 *
	 * Uses a standard {@link Thread#sleep(long) Thread.sleep(long)} function to pause
	 * execution of the program for the specified milliseconds.
	 * Displays an error if the thread is interrupted during
	 * this process, but does not throw an Exception.  (1000 milliseconds = 1 second)
	 *
	 * @param amount The amount of time in milliseconds
	 */
	public void sleep(int amount)
	{
		try
		{
			Thread.sleep(amount);
		}
		catch(Exception e)
		{
			System.err.println("FATAL ERROR: Thread was interrupted! (method: sleep())");
		}
	}

	/**
	 * Attempts to connect to the Arduino/XBee.
	 *
	 * This method attempts to make a serial connection to the Arduino/XBee if
	 * the port is correct.  If there is an error in connecting, then the
	 * appropriate error message will be displayed.
	 *
	 * This function will terminate runtime if an error is discovered.
	 */
	public final void connect()
	{
		System.out.println("RXTXRobot API version " + RXTXRobot.getVersion());
		System.out.println("---------------------------\n");
		System.out.println("Starting up robot, please wait...");
		if ("".equals(port))
		{
			System.err.println("FATAL ERROR: No port was specified to connect to! (method: connect())");
			System.exit(1);
		}
		if (isConnected())
		{
			System.err.println("ERROR: Robot is already connected! (method: connect())");
			return;
		}
		try
		{
			CommPortIdentifier pIdent = CommPortIdentifier.getPortIdentifier(this.port);
			if (pIdent.isCurrentlyOwned())
			{
				System.err.println("FATAL ERROR: Arduino port ("+this.port+") is currently owned by " + pIdent.getCurrentOwner() + "! (method: connect())");
				System.exit(1);
			}
			cPort = pIdent.open("RXTXRobot", 2000);
			if (cPort instanceof SerialPort)
			{
				sPort = (SerialPort)cPort;
				sPort.setSerialPortParams(RXTXRobot.BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				debug("Resetting robot...");
				sleep(500);
				in = sPort.getInputStream();
				out = sPort.getOutputStream();
				System.out.println("Connected...\n");
			}
		}
		catch(NoSuchPortException e)
		{
			System.err.println("FATAL ERROR: Invalid port (NoSuchPortException).  Check to make sure the correct port is set at the object's initialization. (method: connect())");
			if (this.verbose)
			{
				System.err.println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
				e.printStackTrace(System.err);
			}
			System.exit(1);
		}
		catch(PortInUseException e)
		{
			System.err.println("FATAL ERROR: Port is already being used by a different application (PortInUseException).  Did you stop a previously running instance of this program? (method: connect())");
			if (this.verbose)
			{
				System.err.println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
				e.printStackTrace(System.err);
			}
			System.exit(1);
		}
		catch(UnsupportedCommOperationException e)
		{
			System.err.println("FATAL ERROR: Comm Operation is unsupported (UnsupportedCommOperationException).  This shouldn't ever happen.  If you see this, ask a TA for assistance (method: connect())");
			if (this.verbose)
			{
				System.err.println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
				e.printStackTrace(System.err);
			}
			System.exit(1);
		}
		catch(IOException e)
		{
			System.err.println("FATAL ERROR: Could not assign Input and Output streams (IOException).  You may be calling the \"close()\" method before this one.  Make sure you only call \"close()\" at the very end of your program. (method: connect())");
			if (this.verbose)
			{
				System.err.println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
				e.printStackTrace(System.err);
			}
			System.exit(1);
		}
	}
	
	/**
	 * Checks if the RXTXRobot object is connected to the Arduino/XBee.
	 *
	 * Returns true if the RXTXRobot object is successfully connected to the
	 * Arduino/XBee.  Returns false otherwise.
	 *
	 * @return true/false value that specifies if the RXTXRobot object is connected to the Arduino/XBee.
	 */
	public final boolean isConnected()
	{
		return sPort != null && cPort != null && in != null && out != null;
	}

	/**
	 * Closes the connection to the Arduino/XBee.
	 *
	 * This method closes the serial connection to the Arduino/XBee.
	 * It deletes the mutual exclusion lock file, which is important,
	 * so this should be done before the program is terminated.
	 */
	public final void close()
	{
		sleep(300);
		if (sPort != null)
			sPort.close();
		if (cPort != null)
			cPort.close();
		in = null;
		out = null;
	}

	/**
	 * Sends a string to the Arduino to be executed.
	 *
	 * If a serial connection is present, then it sends the String to the
	 * Arduino to be executed.  If verbose is on, then the response from
	 * the Arduino is displayed.
	 *
	 * @param str The command to send to the Arduino
	 */
	public String sendRaw(String str)
	{
		return sendRaw(str,100);
	}
	
	private String sendRaw(String str, int sleep)
	{
		debug("Sending command: " + str);
		if (!isConnected())
		{
			System.err.println("ERROR: Cannot send command because the robot is not connected! (method: sendRaw())");
			return "";
		}
		try 
		{
			out.write((str+"\r\n").getBytes());
			byte[] buffer = new byte[1024];
			sleep(sleep);
			int bytesRead = in.read(buffer);
			String ret = (new String(buffer)).trim();
			debug("Received " + bytesRead + " bytes from the Arduino: " + ret);
			return ret;
		}
		catch(IOException e)
		{
			System.err.println("ERROR: Could not read or use Input or Output streams (IOException).  (method: sendRaw())");
			if (this.verbose)
			{
				System.err.println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
				e.printStackTrace(System.err);
			}
		}
                return "";
	}

	/**
	 * Refreshes the Analog pin cache from the robot.
	 *
	 * This must be called to refresh the data of the pins.
	 */
	public void refreshAnalogPins()
	{
		analogPinCache = new int[RXTXRobot.NUM_ANALOG_PINS];
		for (int x=0; x < analogPinCache.length; ++x)
			analogPinCache[x] = -1;
		try
		{
			String[] split = sendRaw("r a").split("\\s+");
			if (split.length <= 1)
			{
				System.err.println("ERROR: No response was received from the Arduino.  Try again. (method: refreshAnalogPins())");
				return;
			}
			if (split.length-1 != RXTXRobot.NUM_ANALOG_PINS)
			{
				System.err.println("ERROR: Incorrect length returned: " + split.length + ".  (method: refreshAnalogPins())");
				if (this.verbose)
					for (int x=0; x < split.length; ++x)
						System.err.println("["+x+"] = " + split[x]);
				return;
			}
			for (int x=1; x < split.length; ++x)
				analogPinCache[x-1] = Integer.parseInt(split[x]);
		}
		catch (NumberFormatException e)
		{
			System.err.println("ERROR: Returned string could not be parsed into Integers.  (method: refreshAnalogPins())");
		}
		catch (Exception e)
		{
			System.err.println("ERROR: An error occurred with getAnalogPins.");
			if (this.verbose)
			{
				System.err.println("Stacktrace: ");
				e.printStackTrace(System.err);
			}
		}
	}

	/**
	 * Refreshes the digital pin cache from the robot.
	 *
	 * This must be called to refresh the data of the pins.
	 */
	public void refreshDigitalPins()
	{
		digitalPinCache = new int[RXTXRobot.NUM_DIGITAL_PINS];
		for (int x=0; x < digitalPinCache.length; ++x)
			digitalPinCache[x] = -1;
		try
		{
			String[] split = sendRaw("r d").split("\\s+");
			if (split.length <= 1)
			{
				System.err.println("ERROR: No response was received from the Arduino.  Try again. (method: refreshDigitalPins())");
				return;
			}
			if (split.length-1 != RXTXRobot.NUM_DIGITAL_PINS)
			{
				System.err.println("ERROR: Incorrect length returned: " + split.length + ".  (method: refreshDigitalPins())");
				if (this.verbose)
					for (int x=0; x < split.length; ++x)
						System.err.println("["+x+"] = " + split[x]);
				return;
			}
			for (int x=1; x < split.length; ++x)
				digitalPinCache[x-1] = Integer.parseInt(split[x]);
		}
		catch (NumberFormatException e)
		{
			System.err.println("ERROR: Returned string could not be parsed into Integers.  (method: refreshDigitalPins())");
		}
		catch (Exception e)
		{
			System.err.println("ERROR: An error occurred with getDigitalPins.");
			if (this.verbose)
			{
				System.err.println("Stacktrace: ");
				e.printStackTrace(System.err);
			}
		}
	}

	/**
	 * Returns an AnalogPin object for the specified pin.
	 *
	 * This will get the value of the pin since the last time
	 * {@link #refreshAnalogPins() refreshAnalogPins()} was called.
	 *
	 * @param x The number of the pin: 0 &lt; x &lt; {@link #NUM_ANALOG_PINS NUM_ANALOG_PINS}
	 * @return AnalogPin object of the specified pin, or null if error.
	 */
	public AnalogPin getAnalogPin(int x)
	{
		if (analogPinCache == null)
			this.refreshAnalogPins();
		if (x >= analogPinCache.length || x < 0)
		{
			System.err.println("ERROR: Analog pin " + x + " doesn't exist.  (method: getAnalogPin())");
			return null;
		}
		return new AnalogPin(this, x, analogPinCache[x]);
	}

	/**
	 * Returns a DigitalPin object for the specified pin.
	 *
	 * This will get the value of the pin since the last time
	 * {@link #refreshDigitalPins() refreshDigitalPins()} was called.
	 *
	 * @param x The number of the pin: 0 &lt; x &lt; {@link #NUM_DIGITAL_PINS NUM_DIGITAL_PINS}
	 * @return DigitalPin object of the specified pin, or null if error.
	 */
	public DigitalPin getDigitalPin(int x)
	{
		final int[][] mapping = {{13, 0}};
		if (digitalPinCache == null)
			this.refreshDigitalPins();
		int get_pin = -1;
		for (int y=0; y < mapping.length; ++y)
		{
			if (mapping[y][0] == x)
				return new DigitalPin(this, x, digitalPinCache[mapping[y][1]]);
		}
		System.err.println("ERROR: Digital pin " + x + " doesn't exist.  (method: getDigitalPin())");
		return null;
	}

	/**
	 * Return the value of the temperature sensor on digital pin 2.
	 *
	 * An error is displayed if something goes wrong, but verbose is
	 * required for more in-depth errors.
	 *
	 * @return Integer representing the temperature of the water in Celsius.
	 */
	public int getTemperature()
	{
		try
		{
			String[] split = sendRaw("r t").split("\\s+");
			if (split.length <= 1)
			{
				System.err.println("No response was received from the Arduino.  Try again.  (method: getTemperature())");
				return -1;
			}
			if (split.length-1 != 1)
			{
				System.err.println("Incorrect length returned: " + split.length + ".  (method: getTemperature())");
				if (this.verbose)
					for (int x=0; x < split.length; ++x)
						System.err.println("["+x+"] = " + split[x]);
				return -1;
			}
			return Integer.parseInt(split[1]);
		}
		catch (NumberFormatException e)
		{
			System.err.println("ERROR: Returned string could not be parsed into an Integer.  (method: getTemperature())");
		}
		catch (Exception e)
		{
			System.err.println("ERROR: An error occurred with getTemperature()");
			if (this.verbose)
			{
				System.err.println("Stacktrace: ");
				e.printStackTrace(System.err);
			}
		}
		return -1;
	}

	/**
	 * Moves the specified servo to the specified angular position.
	 *
	 * Accepts either {@link #SERVO1 RXTXRobot.SERVO1} or {@link #SERVO2 RXTXRobot.SERVO2}
	 * and an angular position between 0 and 180 inclusive.
	 *
	 * The servo starts at 90 degrees, so a number &lt;90 will turn
	 * it one way, and a number &gt;90 will turn it the other way.
	 * 
	 * An error message will be displayed on error.
	 *
	 * @param servo The servo motor that you would like to move: {@link #SERVO1 RXTXRobot.SERVO1} or {@link #SERVO2 RXTXRobot.SERVO2}.
	 * @param position The position (in degrees) where you want the servo to turn to: 0 &lt; position &lt; 180.
	 */
	public void moveServo(int servo, int position)
	{
		if (servo != RXTXRobot.SERVO1 && servo != RXTXRobot.SERVO2)
		{
			System.err.println("ERROR: Invalid servo argument (RXTXRobot.SERVO1 or RXTXRobot.SERVO2).  (method: moveServo())");
			return;
		}
		debug("Moving servo " + servo + " to position " + position);
		if (position < 0 || position > 180)
			System.err.println("ERROR: Position must be >=0 and <=180.  You supplied " + position + ", which is invalid.  (method: moveServo())");
		else
			sendRaw("v " + servo + " " + position);
	}

	/** 
	 * Moves both servos simultaneously to the desired positions.
	 *
	 * Accepts two angular positions between 0 and 180 inclusive and moves the servo 
	 * motors to the corresponding angular position. {@link #SERVO1 SERVO1} moves {@code pos1} degrees and 
	 * {@link #SERVO2 SERVO2} moves {@code pos2} degrees.
	 *
	 * The servos start at 90 degrees, so a number &lt; 90 will turn
	 * it one way, and a number &gt; 90 will turn it the other way.
	 *
	 * An error message will be displayed on error.
	 *
	 * @param pos1 The angular position of RXTXRobot.SERVO1
	 * @param pos2 The angular position of RXTXRobot.SERVO2
	 */
	public void moveBothServos(int pos1, int pos2)
	{
		debug("Moving both servos to positions " + pos1 + " and " + pos2);
		if (pos1 < 0 || pos1 > 180 || pos2 < 0 || pos2 > 180)
			System.err.println("ERROR: Positions must be >=0 and <=180.  You supplied " + pos1 + " and " + pos2 + ".  One or more are invalid.  (method: moveBothServos())");
		else
			sendRaw("V " + pos1 + " " + pos2);
	}

	/**
	 * Runs a DC motor at a specific speed for a specific time (Potential blocking method).
	 *
	 * Accepts a DC motor, either {@link #MOTOR1 RXTXRobot.MOTOR1}, 
	 * {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3}, 
	 * or {@link #MOTOR4 RXTXRobot.MOTOR4}, the speed that the motor 
	 * should run at (-255 - 255), and the time with which the motor 
	 * should run (in milliseconds).
	 *
	 * If speed is negative, the motor will run in reverse.
	 *
	 * If time is 0, the motor will run infinitely until another call 
	 * to that motor is made, even if the Java program terminates.
	 *
	 * An error message will display on error.
	 *
	 * Note: This method is a blocking method unless time = 0
	 *
	 * @param motor The DC motor you want to run: {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3}, or {@link #MOTOR4 RXTXRobot.MOTOR4}
	 * @param speed The speed that the motor should run at (-255 - 255)
	 * @param time The number of milliseconds the motor should run (0 for infinite) (may not be above 30,000 (30 seconds))
	 */

	public void runMotor(int motor, int speed, int time)
	{
		if (speed < -255 || speed > 255)
		{
			System.err.println("ERROR: You must give the motors a speed between -255 and 255 (inclusive).  (method: runMotor())");
			return;
		}
		if (RXTXRobot.ONLY_ALLOW_TWO_MOTORS)
		{
			boolean prev = motorsRunning[motor];
			if (speed == 0)
				motorsRunning[motor] = false;
			else
				motorsRunning[motor] = true;
			if (!checkRunningMotors())
			{
				motorsRunning[motor] = prev;
				return;
			}
		}
		if (time < 0 || time > 30000)
		{
			System.err.println("ERROR: runMotor was not given a time that is 0 <= time <= 30000.  (method: runMotor())");
			return;
		}
		if (motor < RXTXRobot.MOTOR1 || motor > RXTXRobot.MOTOR4)
		{
			System.err.println("ERROR: runMotor was not given a correct motor argument.  (method: runMotor())");
			return;
		}
		debug("Running motor " + motor + " at speed " + speed + " for time of " + time);
		if (!"".equals(sendRaw("d " + motor + " " + speed + " " + time)))
			sleep(time);
		if (time != 0)
			motorsRunning[motor] = false;
	}

	/**
	 * Runs both DC motors at different speeds for the same amount of time. (Potential blocking method).
	 *
	 * Accepts a DC motor, either {@link #MOTOR1 RXTXRobot.MOTOR1}, 
	 * {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3}, 
	 * or {@link #MOTOR4 RXTXRobot.MOTOR4}, the speed in which that 
	 * motor should run (-255 - 255), accepts another DC motor, the 
	 * speed in which that motor should run, and the time with which 
	 * both motors should run (in milliseconds).
	 *
	 * If speed is negative for either motor, that motor will run in reverse.
	 *
	 * If time is 0, the motors will run infinitely until another 
	 * call to both specific motors is made, even if the Java program terminates.
	 *
	 * An error message will display on error.
	 *
	 * Note: This method is a blocking method unless time = 0
	 *
	 * @param motor1 The first DC motor: {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3}, or {@link #MOTOR4 RXTXRobot.MOTOR4}
	 * @param speed1 The speed that the first DC motor should run at
	 * @param motor2 The second DC motor: {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3}, or {@link #MOTOR4 RXTXRobot.MOTOR4}
	 * @param speed2 The speed that the second DC motor should run at
	 * @param time The amount of time that the DC motors should run (may not be more than 30,000 (30 seconds).
	 */
	public void runMotor(int motor1, int speed1, int motor2, int speed2, int time)
	{
		if (speed1 < -255 || speed1 > 255 || speed2 < -255 || speed2 > 255)
		{
			System.err.println("ERROR: You must give the motors a speed between -255 and 255 (inclusive).  (method: runMotor())");
			return;
		}
		if (RXTXRobot.ONLY_ALLOW_TWO_MOTORS)
		{
			boolean prev1 = motorsRunning[motor1];
			boolean prev2 = motorsRunning[motor2];
			if (speed1 == 0)
				motorsRunning[motor1] = false;
			else
				motorsRunning[motor1] = true;

			if (speed2 == 0)
				motorsRunning[motor2] = false;
			else
				motorsRunning[motor2] = true;

			if (!checkRunningMotors())
			{
				motorsRunning[motor1] = prev1;
				motorsRunning[motor2] = prev2;
				return;
			}
		}
		if (time < 0 || time > 30000)
		{
			System.err.println("ERROR: runMotor was not given a time that is 0 <= time <= 30000.  (method: runMotor())");
			return;
		}
		if ((motor1 < RXTXRobot.MOTOR1 || motor1 > RXTXRobot.MOTOR4) || (motor2 < RXTXRobot.MOTOR1 || motor2 > RXTXRobot.MOTOR4))
		{
			System.err.println("ERROR: runMotor was not given a correct motor argument.  (method: runMotor())");
		}
		debug("Running two motors, motor " + motor1 + " at speed " + speed1 + " and motor " + motor2 + " at speed " + speed2 + " for time of " + time);
		if (!"".equals(sendRaw("D " + motor1 + " " + speed1 + " " + motor2 + " " + speed2 + " " + time)))
			sleep(time);
		if (time != 0)
		{
			motorsRunning[motor1] = false;
			motorsRunning[motor2] = false;
		}
	}

	/**
	 * Runs four DC motors at different speeds for the same amount of time. (Potential blocking method)
	 * Accepts DC motors, either {@link #MOTOR1 RXTXRobot.MOTOR1}, 
	 * {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3}, 
	 * {@link #MOTOR4 RXTXRobot.MOTOR4}, the speed in which those 
	 * motor should run (-255 - 255), accepts another DC motor, 
	 * the speed in which that motor should run, etc, and the 
	 * time with which both motors should run (in milliseconds).
	 *
	 * If speed is negative for any motor, that motor will run in reverse.
	 *
	 * If time is 0, the motors will run infinitely until another 
	 * call to both specific motors is made, even if the Java program terminates.
	 *
	 * An error message will display on error.
	 *
	 * Note: This method is a blocking method unless time = 0
	 *
	 * @param motor1 The first DC motor: {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3}, or {@link #MOTOR4 RXTXRobot.MOTOR4}
	 * @param speed1 The speed that the first DC motor should run at
	 * @param motor2 The second DC motor: {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3}, or {@link #MOTOR4 RXTXRobot.MOTOR4}
	 * @param speed2 The speed that the second DC motor should run at
	 * @param motor3 The third DC motor: {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3}, or {@link #MOTOR4 RXTXRobot.MOTOR4}
	 * @param speed3 The speed that the third DC motor should run at
	 * @param motor4 The fourth DC motor: {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3}, or {@link #MOTOR4 RXTXRobot.MOTOR4}
	 * @param speed4 The speed that the fourth DC motor should run at
	 * @param time The amount of time that the DC motors should run
	 */
	public void runMotor(int motor1, int speed1, int motor2, int speed2, int motor3, int speed3, int motor4, int speed4, int time)
	{
		if (speed1 < -255 || speed1 > 255 || speed2 < -255 || speed2 > 255 || speed3 < -255 || speed3 > 255 || speed4 < -255 || speed4 > 255)
		{
			System.err.println("ERROR: You must give the motors a speed between -255 and 255 (inclusive).  (method: runMotor())");
			return;
		}
		if (RXTXRobot.ONLY_ALLOW_TWO_MOTORS)
		{
			System.err.println("ERROR: You may only run two DC motors at a time, so you cannot use this method!  (method: runMotor())");
			return;
		}
		if (time < 0)
		{
			System.err.println("ERROR: runMotor was not given a time that is >=0.  (method: runMotor())");
			return;
		}
		if ((motor1 < 0 || motor1 > 3) || (motor2 < 0 || motor2 > 3) || (motor3 < 0 || motor3 > 3) || (motor4 < 0 || motor4 > 3))
		{
			System.err.println("ERROR: runMotor was not given a correct motor argument.  (method: runMotor())");
			return;
		}
		debug("Running four motors, motor " + motor1 + " at speed " + speed1 + " and motor " + motor2 + " at speed " + speed2 + " and motor " + motor3 + " at speed " + speed3 + " and motor " + motor4 + " at speed " + speed4 + " for time of " + time);
		if (!"".equals(sendRaw("F " + motor1 + " " + speed1 +" " + motor2 + " " + speed2 + " " + motor3 + " " + speed3 + " " + motor4 + " " + speed4 + " " + time)))
			sleep(time);
	}

	/* This method just checks to make sure that only two DC motors are running */
	private boolean checkRunningMotors()
	{
		int num = 0;
		for (int x=0; x < motorsRunning.length; ++x)
			if (motorsRunning[x])
				++num;
		if (num > 2)
		{
			System.err.println("ERROR: You may not run more than two motors at any given time!");
			return false;
		}
		return true;
	}

	/**
	 * Runs the small, mixing motor for a specific time. (Potential blocking method)
	 *
	 * Accepts a motor location ({@link #MOTOR1 RXTXRobot.MOTOR1}, 
	 * {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3}, 
	 * or {@link #MOTOR4 RXTXRobot.MOTOR4}), and the time with which the 
	 * motor should run (in milliseconds).
	 *
	 * If time is 0, the motor will run infinitely until a call to 
	 * {@link #stopMixer(int) stopMixer}, even if the Java program terminates.
	 *
	 * An error message will display on error.
	 *
	 * Note: This method is a blocking method unless time = 0
	 *
	 * @param motor The motor that the mixer is on: {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3}, or {@link #MOTOR4 RXTXRobot.MOTOR4}
	 * @param time The number of milliseconds the motor should run (0 for infinite)
	 */
	public void runMixer(int motor, int time)
	{
		final int MIXER_SPEED = 30;
		if (motor < RXTXRobot.MOTOR1 || motor > RXTXRobot.MOTOR4)
		{
			System.err.println("ERROR: You must supply a valid motor port: RXTXRobot.MOTOR1, MOTOR2, MOTOR3, or MOTOR4.  (method: runMixer())");
			return;
		}
		if (time < 0)
		{
			System.err.println("ERROR: You must supply a positive time.  (method: runMixer())");
			return;
		}
		debug("Running mixer on port " + motor + " at speed " + MIXER_SPEED + " for time of " + time);
		if (!"".equals(sendRaw("d " + motor + " " + MIXER_SPEED + " " + time)))
			sleep(time);
	}

	/**
	 * Stops the small, mixing motor if it is currently running.
	 *
	 * This method should be called if {@link #runMixer(int) runMixer} 
	 * was called with a time of 0.  This method will stop the mixer.
	 *
	 * @param motor The motor that the mixer is on: {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3}, or {@link #MOTOR4 RXTXRobot.MOTOR4}
	 */

	public void stopMixer(int motor)
	{
		if (motor < RXTXRobot.MOTOR1 || motor > RXTXRobot.MOTOR4)
		{
			System.err.println("ERROR: You must supply a valid motor port: RXTXRobot.MOTOR1, MOTOR2, MOTOR3, or MOTOR4.  (method: stopMixer())");
			return;
		}
		debug("Stopping mixer on port " + motor);
		sendRaw("d " + motor + " 0 0");
	}
}

