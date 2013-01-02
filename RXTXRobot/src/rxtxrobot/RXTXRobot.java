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

public class RXTXRobot
{
	final private static String API_VERSION = "3.0.0";
	final private static boolean ONLY_ALLOW_TWO_MOTORS = true;
	final private static int BAUD_RATE = 9600;

	final public static int SERVO1 = 1;
	final public static int SERVO2 = 0;
	final public static int MOTOR1 = 0;
	final public static int MOTOR2 = 1;
	final public static int MOTOR3 = 2;
	final public static int MOTOR4 = 3;
	
	final public static int NUM_DIGITAL_PINS = 1;
	final public static int NUM_ANALOG_PINS = 6;

	private String port = "";
	private boolean verbose = false;
	private int[] analogPinCache = null;
	private int[] digitalPinCache = null;
	private boolean[] motorsRunning = {false, false, false, false};

	private InputStream in;
	private OutputStream out;
	private SerialPort sPort;
	private CommPort cPort;

	public RXTXRobot()
	{
	}

	public static String getVersion()
	{
		return API_VERSION;
	}

	public void setOutStream(PrintStream p)
	{
		System.setOut(p);
	}

	public void setErrStream(PrintStream p)
	{
		System.setErr(p);
	}

	public void setVerbose(boolean v)
	{
		this.verbose = v;
	}

	public void setPort(String p)
	{
		this.port = p;
	}

	public String getPort()
	{
		return this.port;
	}

	private void debug(String str)
	{
		if (verbose)
			System.out.println("--> " + str);
	}
	
	private void debugErr(String str)
	{
		if (verbose)
			System.err.println("--> " + str);
	}

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
				e.printStackTrace(System.getErr);
			}
			System.exit(1);
		}
		catch(PortInUseException e)
		{
			System.err.println("FATAL ERROR: Port is already being used by a different application (PortInUseException).  Did you stop a previously running instance of this program? (method: connect())");
			if (this.verbose)
			{
				System.err.println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
				e.printStackTrace(System.getErr());
			}
			System.exit(1);
		}
		catch(UnsupportedCommOperationException e)
		{
			System.err.println("FATAL ERROR: Comm Operation is unsupported (UnsupportedCommOperationException).  This shouldn't ever happen.  If you see this, ask a TA for assistance (method: connect())");
			if (this.verbose)
			{
				System.err.println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
				e.printStackTrace(System.getErr());
			}
			System.exit(1);
		}
		catch(IOException e)
		{
			System.err.println("FATAL ERROR: Could not assign Input and Output streams (IOException).  You may be calling the \"close()\" method before this one.  Make sure you only call \"close()\" at the very end of your program. (method: connect())");
			if (this.verbose)
			{
				System.err.println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
				e.printStackTrace(System.getErr());
			}
			System.exit(1);
		}
	}

	public final boolean isConnected()
	{
		return sPort != null && cPort != null && in != null && out != null;
	}

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

	public String sendRaw(String str)
	{
		return sendRaw(str,100);
	}
	
	public String sendRaw(String str, int sleep)
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
				e.printStackTrace(System.getErr());
			}
		}
	}

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
			return;
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
				e.printStackTrace(System.getErr());
			}
		}
	}

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
			return;
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
				e.printStackTrace(System.getErr());
			}
		}
	}

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
				e.printStackTrace(System.getErr());
			}
		}
		return -1;
	}

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

	public void moveBothServos(int pos1, int pos2)
	{
		debug("Moving both servos to positions " + pos1 + " and " + pos2);
		if (pos1 < 0 || pos1 > 180 || pos2 < 0 || pos2 > 180)
			System.err.println("ERROR: Positions must be >=0 and <=180.  You supplied " + pos1 + " and " + pos2 + ".  One or more are invalid.  (method: moveBothServos())");
		else
			sendRaw("V " + pos1 + " " + pos2);
	}

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

