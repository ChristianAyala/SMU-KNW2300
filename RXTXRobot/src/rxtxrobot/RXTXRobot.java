package rxtxrobot;

/*  RXTXRobot API package
 *   
 *  All the methods in this package are asyncronous, unless otherwise noted.
 * 
 * 
 *   public:
 *     +RXTXRobot(String arduino_port, [String labview_port], [boolean verbose])
 *     +connect()
 *     +isConnected()
 *     +close()
 *     +sendRaw(String str)
 *     +getLastResponse()
 *     +sleep(int length)
 *     +getAnalogPins()
 *     +getAnalogPin(int index)
 *     +getDigitalPins()
 *     +getDigitalPin(int index)
 *     +setAnalogPin(int pin, int value)
 *     +setDigitalPin(int pin, int value)
 *     +moveServo(int servo, int position)
 *     +moveBothServos(int position_1, int position_2)
 *     +moveStepper(int stepper, int steps)
 *     +runMotor(int motor_1, int speed_1, [int motor_2, int speed_2], int time)
 * 
 *   private:
 *     -debug(String str)
 */

import gnu.io.*;
import java.io.*;

/**
 * @author Chris King
 */
public class RXTXRobot
{
    /* Constants */
    /**
     * Refers to the servo motor located in SERVO1
     */
    final public static int SERVO1 = 1;
    /**
     * Refers to the servo motor located in SERVO2
     */
    final public static int SERVO2 = 0;
    /**
     * Refers to the the M3 DC motor on the Arduino
     */
    final public static int MOTOR1 = 2;
    /**
     * Refers to the M4 DC motor on the Arduino
     */
    final public static int MOTOR2 = 3;
    /**
     * Refers to the M1/M2 Stepper motor on the Arduino
     */
    final public static int STEPPER1 = 0;
    /**
     * The maximum number of digital pins that can be read from the Arduino (0&nbsp;&le;&nbsp;pins&nbsp;&lt;&nbsp;NUM_DIGITAL_PINS)
     */
    final public static int NUM_DIGITAL_PINS = 12;
    /**
     * The maximum number of analog pins that can be read from the Arduino (0&nbsp;&le;&nbsp;pins&nbsp;&lt;&nbsp;NUM_ANALOG_PINS)
     */
    final public static int NUM_ANALOG_PINS = 6;
    /* Private variables */
    private String arduino_port;
    private String labview_port;
    private boolean verbose;
    private OutputStream a_out;
    private InputStream a_in;
    private OutputStream l_out;
    private InputStream l_in;
    private byte[] buffer;
    private String lastResponse;
    private SerialPort a_serialPort;
    private SerialPort l_serialPort;
    private CommPort a_commPort;
    private CommPort l_commPort;
    private int stepsPerRotation;
    private byte errorFlag = 0;
    final private static int bufferSize = 1024;
    /**
     * Accepts the arduino port.
     * 
     * Accepts a port name for the Arduino, does not connect to LabView, and sets the verbose debugging
     * to false. 
     * 
     * <br /><br />The port name will be:<br />
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Windows:</b> "COM3" (or "COM4" or "COM5", check device manager to see)<br />
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Mac/Linux:</b> "/dev/ttyACM0"
     * 
     * @param arduino_port Port name the Arduino/XBee is connected to.
     */
    public RXTXRobot(String arduino_port)
    {
        this.arduino_port = arduino_port;
        this.labview_port = "";
        this.stepsPerRotation = -1;
        this.verbose = false;
        connect();
    }
    /**
     * Accepts the arduino port, and verbose debugging boolean.
     * 
     * <br /><br />The port name will be:<br />
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Windows:</b> "COM3" (or "COM4" or "COM5", check device manager to see)<br />
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Mac/Linux:</b> "/dev/ttyACM0"
     * 
     * @param arduino_port Port name the Arduino/XBee is connected to
     * @param verbose Boolean value that allows for descriptive debugging messages
     */
    public RXTXRobot(String arduino_port, boolean verbose)
    {
        this.arduino_port = arduino_port;
        this.labview_port = "";
        this.stepsPerRotation = -1;
        this.verbose = verbose;
        connect();
    }
    /**
     * Accepts the arduino port, and the labview port.
     * 
     * Accepts a port name for the Arduino, a port name for LabView, and sets the verbose debugging
     * to false. 
     * 
     * <br /><br />The port name will be:<br />
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Windows:</b> "COM3" (or "COM4" or "COM5", check device manager to see)<br />
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Mac/Linux:</b> "/dev/ttyACM0"
     * 
     * @param arduino_port Port name the Arduino/XBee is connected to.
     * @param labview_port Port name the LabView/XBee is connected to.
     */
    public RXTXRobot(String arduino_port, String labview_port)
    {
        this.arduino_port = arduino_port;
        this.labview_port = labview_port;
        this.stepsPerRotation = -1;
        this.verbose = false;
        connect();
    }
    /**
     * Accepts the arduino port, and the labview port, and verbose debugging boolean.
     * 
     * <br /><br />The port name will be:<br />
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Windows:</b> "COM3" (or "COM4" or "COM5", check device manager to see)<br />
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Mac/Linux:</b> "/dev/ttyACM0"
     * 
     * @param arduino_port Port name the Arduino/XBee is connected to
     * @param labview_port Port name the LabView/XBee is connected to
     * @param verbose Boolean value that allows for descriptive debugging messages
     */
    public RXTXRobot(String arduino_port, String labview_port, boolean verbose)
    {
        this.arduino_port = arduino_port;
        this.labview_port = labview_port;
        this.verbose = verbose;
        this.stepsPerRotation = -1;
        connect();
    }
    // Secret constructor
    public RXTXRobot(String arduino_port, String labview_port, boolean verbose, int spr)
    {
        this.arduino_port = arduino_port;
        this.labview_port = labview_port;
        this.verbose = verbose;
        this.stepsPerRotation = spr;
        connect();
    }
    /**
     * 
     * Attempts to connect to the Arduino/XBee and the LabView/XBee.
     * 
     * This method identifies the port that is currently being used by the Arduino/XBee 
     * and makes a serial connection to the Arduino if the port is not already in use.
     * If there is an error in connecting, then a different error message will be 
     * displayed to the user for each case.
     * 
     * This function does not terminate runtime if an error is discovered.  See the
     * function {@link isConnected()} to test for an active connection.
     * 
     */
    public final void connect()
    {
        if (!isConnected())
        {
            try
            {
                CommPortIdentifier a_portIdentifier = CommPortIdentifier.getPortIdentifier(arduino_port);
                CommPortIdentifier l_portIdentifier = null;
                if (!labview_port.equals(""))
                    l_portIdentifier = CommPortIdentifier.getPortIdentifier(labview_port);
                if (a_portIdentifier.isCurrentlyOwned())
                {
                    System.err.println("Error: Arduino port is currently in use ("+arduino_port+")");
                }
                else if (!labview_port.equals("") && l_portIdentifier.isCurrentlyOwned())
                {
                    System.err.println("Error: Labview port is currently in use ("+labview_port+")");
                }
                else
                {
                        a_commPort = a_portIdentifier.open("RXTXRobot",2000);
                        if (!labview_port.equals(""))
                            l_commPort = l_portIdentifier.open("LabView",2000);
                        if ( a_commPort instanceof SerialPort && (labview_port.equals("") || l_commPort instanceof SerialPort ))
                        {
                            a_serialPort = (SerialPort) a_commPort;
                            if (!labview_port.equals(""))
                                l_serialPort = (SerialPort) l_commPort;
                            a_serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                            if (!labview_port.equals(""))
                                l_serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                            debug("Sleeping and resetting...");
                            Thread.sleep(3000);
                            debug("Waking up...");
                            a_in = a_serialPort.getInputStream();
                            a_out = a_serialPort.getOutputStream();
                            if (!labview_port.equals(""))
                            {
                                l_in = l_serialPort.getInputStream();
                                l_out = l_serialPort.getOutputStream();
                            }
                        }
                }
            }
            catch(NoSuchPortException e)
            {
                System.err.println("Invalid port (NoSuchPortException). Check to make sure that the correct port is set at the objects initialization.");
                if (verbose)
                {
                    System.err.println("Error Message: " + e.toString()+"\n\nError StackTrace:\n");
                    e.printStackTrace();
                }
            }
            catch(PortInUseException e)
            {
                System.err.println("Port is already being used by a different application (PortInUseException). Did you stop a previously running instance of this program?");
                if (verbose)
                {
                    System.err.println("Error Message: " + e.toString()+"\n\nError StackTrace:\n");
                    e.printStackTrace();
                }
            }
            catch(UnsupportedCommOperationException e)
            {
                System.err.println("Comm Operation is unsupported (UnsupportedCommOperationException).  This error shouldn't really happen, ever.  If you see this, ask a TA for assistance");
                if (verbose)
                {
                    System.err.println("Error Message: " + e.toString()+"\n\nError StackTrace:\n");
                    e.printStackTrace();
                }
            }
            catch(InterruptedException e)
            {
                System.err.println("Thread was interrupted (InterruptedException).  Something stopped the Thread from executing (early termination of program?).  If you meant to terminate the program, ignore this error.");
                if (verbose)
                {
                    System.err.println("Error Message: " + e.toString()+"\n\nError StackTrace:\n");
                    e.printStackTrace();
                }
            }
            catch(IOException e)
            {
                System.err.println("Could not assign Input and Output streams (IOException).  This should never happen, unless on rare instances.  Try unplugging and replugging in the Arduino/XBee again, then re-run the program.  If that doesn't fix the problem, get a TA for assistance");
                if (verbose)
                {
                    System.err.println("Error Message: " + e.toString()+"\n\nError StackTrace:\n");
                    e.printStackTrace();
                }
            }
        }
        else
            System.err.println("connect() - RXTXRobot is already connected.");
    }
    /**
     * 
     * Checks if the RXTXRobot object is connected to the Arduino/XBee AND LabView/XBee.
     * 
     * Returns true if the RXTXRobot object is successfully connected to the Arduino/XBee and LabView/XBee.  Returns false otherwise.
     * 
     * @return true/false value that specifies if the RXTXRobot object is connected to the Arduino/XBee and LabView/XBee
     */
    public final boolean isConnected()
    {
        return a_serialPort != null && a_commPort != null && (labview_port.equals("") || (l_serialPort != null && l_commPort != null));
    }
    /**
     * 
     * Closes the connection to the Arduino/XBee and LabView/XBee.
     * 
     * This method closes the serial connection to the Arduino/XBee and LabView/XBee.  It deletes the mutual exclusion lock
     * file which is important, so this should be done before the program is terminated.
     * 
     */
    public final void close()
    {
        sleep(300);
        debug("Resetting servos to position of 90 degrees for next run's use");
        this.moveBothServos(90,90);
        if (a_serialPort != null)
            a_serialPort.close();
        if (a_commPort != null)
            a_commPort.close();
        if (!labview_port.equals(""))
        {
            if (l_serialPort != null)
                l_serialPort.close();
            if (l_commPort != null)
                l_commPort.close();
        }
    }
    
    /**
     * Prints out debugging statements.
     * 
     * If verbose is set to true in the constructor then debugging statements will 
     * be printed out to the user. If verbose is set to false then these statements 
     * will not print. 
     * 
     * @param s message passed to debug
     */
    private void debug(String s)
    {
        if (verbose)
            System.out.println("----> " + s);
    }
    /**
     * 
     * Sends a string to the Arduino to be executed.
     * 
     * If a serial connection is present, then it sends the string s to the Arduino to be executed.
     * If verbose is true then the response from the Arduino is read into lastResponse. 
     * 
     * @param str The command sent to the Arduino 
     */
    public void sendRaw(String str)
    {
        sendRaw(str,200);
    }
    
    private void sendRaw(String str,int sleep)
    {
        if (errorFlag != 2)
            errorFlag = 0;
        debug("Sending command: " + str);
        try
        {
            if (a_out != null && a_in != null && isConnected())
            {
                a_out.write((str+"\r\n").getBytes());
                //if (verbose && errorFlag != 2)
                //{  
                buffer = new byte[bufferSize];
                sleep(sleep);
                a_in.read(buffer, 0, Math.min(a_in.available(), buffer.length));
                lastResponse = (new String(buffer)).trim();
                //}
            }
            else
                errorFlag = 1;
        }
        catch(IOException e)
        {
            if (errorFlag != 2)
            {
                errorFlag = 1;
                System.err.println("IGNORE IF STEPPER COMMAND CAUSES THIS ERROR.  Could not use Input and Output streams (IOException).  This should never happen, unless on rare instances.  Try unplugging and replugging in the Arduino/XBee again, then re-run the program.  If that doesn't fix the problem, get a TA for assistance");
                if (verbose)
                {
                    System.err.println("Error Message: " + e.toString()+"\n\nError StackTrace:\n");
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * Reads the Wii remote data from LabView.
     * 
     * @return The response from LabView in a {@link Coord} object or null on error.
     */
    public Coord readFromLabView()
    {
        if (labview_port.equals(""))
        {
            System.err.println("A connection to labview was not made.  Cannot read from labview.");
            return null;
        }
        String command = "s";
        debug("Sending command: " + command);
        try
        {
            if (l_out != null && l_in != null && isConnected())
            {
                
                buffer = new byte[bufferSize];
                l_out.write((command).getBytes());
                sleep(800);
                l_in.read(buffer, 0, Math.min(l_in.available(), buffer.length));
                lastResponse = new String(buffer);
                debug("XBee Response: " + lastResponse);
                if (lastResponse.indexOf(",")==-1)
                {
                    System.err.println("No response from LabView.  Returning a null object");
                    return null;
                }
                lastResponse = lastResponse.substring(lastResponse.indexOf("[")+1, lastResponse.indexOf("]"));
                String[] parts = lastResponse.split(",");
                debug("Creating Coord with x="+parts[0]+", y="+parts[1]+", z="+parts[2]);
                return new Coord(Double.parseDouble(parts[0]),Double.parseDouble(parts[1]),Double.parseDouble(parts[2]));
            }
        }
        catch(NumberFormatException e)
        {
            debug("Labview returned: " + lastResponse + " and determined it not to be a number.");
            return null;
        }
        catch(Exception e)
        {
            System.err.println("A generic error occurred: Error: " + e.toString());
        }
        return null;
    }
    public Coord[] readFromLabView2()
    {
        if (labview_port.equals(""))
        {
            System.err.println("A connection to labview was not made.  Cannot read from labview.");
            return new Coord[0];
        }
        String command = "s";
        debug("Sending command: " + command);
        try
        {
            if (l_out != null && l_in != null && isConnected())
            {
                
                buffer = new byte[bufferSize];
                l_out.write((command).getBytes());
                sleep(800);
                l_in.read(buffer, 0, Math.min(l_in.available(), buffer.length));
                lastResponse = new String(buffer);
                debug("XBee Response: " + lastResponse);
                if (lastResponse.indexOf(",")==-1)
                {
                    System.err.println("No response from LabView.  Returning an empty Coord array.");
                    Coord[] ans = {new Coord(-1.0,-1.0,-1.0), new Coord(-1.0,-1.0,-1.0)};
                    return ans;
                }
                lastResponse = lastResponse.substring(lastResponse.indexOf("[")+1, lastResponse.indexOf("]"));
                String[] parts = lastResponse.split(",");
                debug("Creating Coord with x1="+parts[0]+", y1="+parts[1]+", x2="+parts[2]+", y2="+parts[3]);
                Coord[] ans = {new Coord(Double.parseDouble(parts[0]),Double.parseDouble(parts[1]),-1.0),new Coord(Double.parseDouble(parts[2]),Double.parseDouble(parts[3]),-1.0)};
                return ans;
            }
        }
        catch(NumberFormatException e)
        {
            debug("Labview returned: " + lastResponse + " and determined it not to be a number.");
            Coord[] ans = {new Coord(-1.0,-1.0,-1.0), new Coord(-1.0,-1.0,-1.0)};
            return ans;
        }
        catch(Exception e)
        {
            System.err.println("A generic error occurred: Error: " + e.toString());
        }
        Coord[] ans = {new Coord(-1.0,-1.0,-1.0), new Coord(-1.0,-1.0,-1.0)};
        return ans;
    }
    /**
     * 
     * Returns the last response sent from the Arduino.
     * 
     * <br /><br /><b>This should only be relied on when verbose mode is set.</b>
     * 
     * @return The last response sent from the Arduino.
     */
    public String getLastResponse()
    {
        return lastResponse;
    }
    /**
     * 
     * Allows the robot to sleep for time length measured in milliseconds.
     * 
     * Uses a standard Thread.sleep() function to pause execution of the program for the 
     * specified milliseconds. Displays an error if the thread is interrupted during 
     * this process, but does not throw an Exception. (1000 milliseconds = 1 second)
     * 
     * @param length The amount of time in milliseconds
     */
    public void sleep(int length)
    {
        try
        {
            Thread.sleep(length);
        }
        catch(InterruptedException e)
        {
            System.err.println("Thread was interrupted (InterruptedException). Error: " + e.toString());
        }
    }
    /**
     * 
     * Returns the 6 analog pin values from the Arduino in String form. <br /><br />
     * 
     * Each pin's value is returned in one string, separated by a space.  Therefore,
     * the format of the string is:<br /><br />
     * 
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i>0_pin 1_pin 2_pin 3_pin 4_pin 5_pin</i><br /><br />
     * 
     * Where #_pin is an integer containing the pin's current value.  The string is
     * stored into lastResponse for future reference if needed.  An error is displayed to
     * the user if an Exception is internally thrown, but execution does not stop.
     * 
     * @return The output of the 6 analog pins in string form or an empty String on error.
     */
    public String getAnalogPins()
    {
        try
        {
            if (a_out != null && a_in != null && isConnected())
            {
                //buffer = new byte[bufferSize];
                debug("Reading Analog Pins...");
                sendRaw("r a");
                //sleep(200);
                //a_in.read(buffer, 0, Math.min(a_in.available(), buffer.length));
                //lastResponse = (new String(buffer)).trim();
                debug("Received response: " + lastResponse);
                return lastResponse;
            }
        }
        catch(Exception e)
        {
            System.err.println("Cannot read command (IOException)! Error: " + e.toString());
        }
        return "";
    }
    /**
     * 
     * Returns the 6 analog pins from the Arduino in an Integer array. <br /><br />
     * 
     * Each pin's value is returned in a zero-indexed integer array.
     * 
     * An error is displayed if something goes wrong, but verbose is required for more in-depth errors.
     * 
     * @return Integer array of size RXTXRobot.NUM_ANALOG_PINS filled with the Analog pin's value (or -1 on error).
     */
    public int[] getAllAnalogPins()
    {
        int[] ans = new int[RXTXRobot.NUM_ANALOG_PINS];
        for (int x=0;x<ans.length;++x)
            ans[x] = -1;
        try
        {
            String pins = this.getAnalogPins();
            String[] split = pins.split("\\s+");
            if (split.length == 1)
            {
                System.err.println("getAllAnalogPins() - No response was received from the Arduino.  Try again");
                return ans;
            }
            if (split.length-1 != RXTXRobot.NUM_ANALOG_PINS)
            {
                System.err.println("getAllAnalogPins() - Incorrect length returned: " + split.length);
                if (verbose)
                    for (int x=0;x<split.length;++x)
                        System.err.println("["+x+"] = " + split[x]);
                return ans;
            }
            //ans = new int[split.length-1];
            for (int x=1;x<split.length;++x)
            {
                ans[x-1] = Integer.parseInt(split[x]);
            }
            return ans;
        }
        catch (Exception e)
        {
            System.err.println("An error occurred with getAllAnalogPins.");
            if (verbose)
            {
                System.err.println("Stacktrace: ");
                e.printStackTrace();
            }
        }
        return ans;
    }
    /**
     * Returns the 12 digital pin values from the Arduino in String form. <br /><br />
     * 
     * Each pin's value is returned in one string, separated by a space.  Therefore,
     * the format of the string is:<br /><br />
     * 
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i>0_pin 1_pin 2_pin 3_pin ... 11_pin</i><br /><br />
     * 
     * Where #_pin is an integer containing the pin's current value.  The string is
     * stored into lastResponse for future reference if needed.  An error is displayed to
     * the user if an Exception is internally thrown, but execution does not stop.
     * 
     * @return The output of the 12 digital pins in string form or an empty String on error.
     */
    public String getDigitalPins()
    {
        try
        {
            if (a_out != null && a_in != null && isConnected())
            {
                //buffer = new byte[bufferSize];
                debug("Reading Digital Pins...");
                sendRaw("r d");
                //sleep(200);
                //a_in.read(buffer, 0, Math.min(a_in.available(), buffer.length));
                //lastResponse = (new String(buffer)).trim();
                debug("Received response: " + lastResponse);
                return lastResponse;
            }
        }
        catch(Exception e)
        {
            System.err.println("Cannot read command (IOException)! Error: " + e.toString());
        }
        return "";
    }
    /**
     * 
     * Returns the 12 digital pins from the Arduino in an Integer array. <br /><br />
     * 
     * Each pin's value is returned in a zero-indexed integer array.
     * 
     * An error is displayed if something goes wrong, but verbose is required for more in-depth errors.
     * 
     * @return Integer array of size RXTXRobot.NUM_DIGITAL_PINS filled with the Digital pin's value (or -1 on error)
     */
    public int[] getAllDigitalPins()
    {
        int[] ans = new int[RXTXRobot.NUM_DIGITAL_PINS];
        for (int x=0;x<ans.length;++x)
            ans[x] = -1;
        try
        {
            String pins = this.getDigitalPins();
            String[] split = pins.split("\\s+");
            if (split.length == 1)
            {
                System.err.println("getAllDigitalPins() - No response was received from the Arduino.  Try again");
                return ans;
            }
            if (split.length-1 != RXTXRobot.NUM_DIGITAL_PINS)
            {
                System.err.println("getAllDigitalPins() - Incorrect length returned: " + split.length);
                if (verbose)
                    for (int x=0;x<split.length;++x)
                        System.err.println("["+x+"] = " + split[x]);
                return ans;
            }
            //ans = new int[split.length-1];
            for (int x=1;x<split.length;++x)
            {
                ans[x-1] = Integer.parseInt(split[x]);
            }
            return ans;
        }
        catch (Exception e)
        {
            System.err.println("An error occurred with getAllDigitalsPins.");
            if (verbose)
            {
                System.err.println("Stacktrace: ");
                e.printStackTrace();
            }
        }
        return ans;
    }
    /**
     * Returns the value of the digital pin specified by index.
     * <br /><br />
     * <b>Index must be: 0 &le; index &lt; {@link NUM_DIGITAL_PINS}</b>
     * 
     * @param index The pin number that you want to read from
     * @return The value of the specified pin number or -1 on error.
     */
    public int getDigitalPin(int index)
    {
        try
        {
            String pins = this.getDigitalPins();
            String[] split = pins.split("\\s+");
            if (index >=RXTXRobot.NUM_DIGITAL_PINS || index<0)
                System.err.println("ERROR: getDigitalPin was given an index that is not within the range of 0 to "+(RXTXRobot.NUM_DIGITAL_PINS-1)+" (inclusive)");
            else
                return Integer.parseInt(split[index+1]);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.err.println("getDigitalPin() did not get a response from the Arduino board.  Try again.");
            debug("ERROR: "+e.toString());
            return -1;
        }
        return -1;
    }
    /**
     * Returns the value of the analog pin specified by index.
     * <br /><br />
     * <b>Index must be: 0 &le; index &lt; {@link NUM_ANALOG_PINS}</b>
     * 
     * @param index The pin number that you want to read from
     * @return The value of the specified pin number or -1 on error.
     */
    public int getAnalogPin(int index)
    {
        try
        {
            String pins = this.getAnalogPins();
            String[] split = pins.split("\\s+");
            if (index >=RXTXRobot.NUM_ANALOG_PINS || index<0)
                System.err.println("ERROR: getAnalogPin was given an index that is not within the range of 0 to "+(RXTXRobot.NUM_ANALOG_PINS-1)+" (inclusive)");
            else
                return Integer.parseInt(split[index+1]);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.err.println("getAnalogPin() did not get a response from the Arduino board.  Try again.");
            debug("ERROR: "+e.toString());
            return -1;
        }
        return -1;
    }
    /**
     * 
     * Sets the value of the specified analog pin.
     * 
     * Sets the specified analog pin to the specified value.
     * <br /><br /><b>Index must be: 0 &le; index &lt; {@link NUM_ANALOG_PINS}</b>
     * 
     * @param pin The analog pin number to set.
     * @param value The value that you would like to set the pin to.
     */
    public void setAnalogPin(int pin, int value)
    {
        debug("Setting Analog Pin " + pin + " to " + value);
        sendRaw("w a " + pin + " " + value);
    }
    /**
     * 
     * Sets the value of the digital pin.
     * 
     * Sets the specified digital pin to the specified value.
     * <br /><br /><b>Index must be: 0 &le; index &lt; {@link NUM_DIGITAL_PINS}</b>
     * 
     * @param pin The analog pin number to set.
     * @param value The value that you would like to set the pin to.
     */
    public void setDigitalPin(int pin, int value)
    {
        debug("Setting Digital Pin " + pin + " to " + value);
        sendRaw("w d " + pin + " " + value);
    }
    /**
     * 
     * Moves the specified servo to the specified angular position.
     * 
     * Accepts either RXTXRobot.SERVO1 or RXTXRobot.SERVO2 and an angular position between 0 and 180 exclusive.
     * <br /><br />
     * The servo starts at 90 degrees, so a number &lt; 90 will turn it one way, and a number &gt; 90 will turn
     * it the other way.  An error message will be displayed on error.
     * 
     * @param servo The servo motor that you would like to move: RXTXRobot.SERVO1 or RXTXRobot.SERVO2.
     * @param position The position (in degrees) where you want the servo to turn to: 0 &lt; position &lt; 180.
     */
    public void moveServo(int servo, int position)
    {
        if (servo != RXTXRobot.SERVO1 && servo != RXTXRobot.SERVO2)
        {
            System.err.println("ERROR: moveServo: Invalid servo argument (RXTXRobot.SERVO1 or RXTXRobot.SERVO2");
            return;
        }
        debug("Moving servo " + servo + " to position " + position);
        if (position <= 0 || position >= 180)
            System.err.println("ERROR: moveServo position must be greater than 0 and less than 180.  You supplied " + position + ", which is invalid.");
        else
            sendRaw("v " + servo + " " + position);
    }
    /**
     * 
     * Moves both servos simultaneously to the desired positions.
     * 
     * Accepts two angular positions between 0 and 180 exclusive and moves the servo 
     * motors to the corresponding angular position. SERVO1 moves pos1 degrees and 
     * SERVO2 moves pos2 degrees.
     * <br /><br />
     * The servos start at 90 degrees, so a number &lt; 90 will turn it one way, and a number &gt; 90 will turn
     * it the other way.  An error message will be displayed on error.
     * 
     * @param pos1 The angular position of RXTXRobot.SERVO1
     * @param pos2 The angular position of RXTXRobot.SERVO2
     */
    public void moveBothServos(int pos1, int pos2)
    {
        debug("Moving both servos to positions " + pos1 + " and " + pos2);
        if (pos1 <= 0 || pos1 >= 180 || pos2 <= 0 || pos2 >= 180)
            System.err.println("ERROR: moveBothServos positions must be greater than 0 and less than 180.  You supplied " + pos1 + " and " + pos2 + ".  One or more are invalid.");
        else
            sendRaw("V " + pos1 + " " + pos2);
    }
    /**
     * 
     * Move the stepper motor a specified number of steps (<b>Blocking method</b>).
     * 
     * Accepts a stepper motor (M1 and M2 on the Ardiuno board) and a number of steps
     * that the stepper motor should turn.  Negative steps will rotate
     * counter-clockwise and positive steps will rotate clockwise.<br /><br />
     * 
     * One step = 15 degrees. (<i>IE: 24 steps for one full revolution</i>)<br /><br />
     * 
     * <b>Note: This method is a blocking method.</b>
     * 
     * @param stepper The stepper motor that you want to move ({@link RXTXRobot.STEPPER1})
     * @param steps The number of steps you want the motor to move
     */
    public void moveStepper(int stepper, int steps)
    {
        if(stepper != RXTXRobot.STEPPER1){
            System.err.println("ERROR: moveStepper was not given the correct stepper argument (RXTXRobot.STEPPER1)");
            return;
        }
        debug("Moving stepper " + stepper + " " + steps + " steps");
        errorFlag = 2;
        sendRaw("p " + stepper + " " + steps);
        if (errorFlag == 2)
            errorFlag = 0;
        if (errorFlag == 0)
        {
            if(stepsPerRotation < 0)
                sleep((int)(Math.abs(steps)*60*1000*((24.0/100))/(24*30))); // Old sleep method
            else
                sleep((int)((Math.abs(steps)*60000)/(30*stepsPerRotation))); // steps * (1 rot/# steps) * (1 min / 30 rot) * (60 sec / 1 min) * (1000 ms / 1 sec)
        }
    }
    /**
     * 
     * Runs a DC motor at a specific speed for a specific time. (<b>Potential blocking method</b>)
     *   
     * Accepts a DC motor, either RXTXRobot.MOTOR1 or RXTXRobot.MOTOR2, the speed 
     * that the motor should run at (arbitrary units), and the time with which the motor should run (in milliseconds).
     * <br /><br />
     * If speed is negative, the motor will run in reverse.
     * <br /><br />
     * If time is 0, the motor will run infinitely until another call to that motor is made, even if the Java program terminates.
     * 
     * <br /><br />An error message will display on error.<br /><br />
     * 
     * <b>Note: This method is a blocking method <u>unless</u> time = 0</b>
     * 
     * @param motor The DC motor you want to run: RXTXRobot.MOTOR1 or RXTXRobot.MOTOR2
     * @param speed The speed that the motor should run at (arbitrary units)
     * @param time The number of milliseconds the motor should run (0 for infinite)
     */
    public void runMotor(int motor, int speed, int time)
    {
        if (time < 0)
        {
            System.err.println("ERROR: runMotor was not given a time that is >=0");
            return;
        }
        if (motor != RXTXRobot.MOTOR1 && motor != RXTXRobot.MOTOR2)
        {
            System.err.println("ERROR: runMotor was not given a correct motor argument");
            return;
        }
        debug("Running motor " + motor + " at speed " + speed + " for time of " + time);
        sendRaw("d " + motor + " "  + speed + " " + time);
        if (errorFlag == 0)
            sleep(time);
    }
    /**
     * 
     * Runs both DC motors at different speeds for the same amount of time. (<b>Potential blocking method</b>)
     *   
     * Accepts a DC motor, either RXTXRobot.MOTOR1 or RXTXRobot.MOTOR2, the speed 
     * in which that motor should run (arbitrary units), accepts another DC motor, the speed in which
     * that motor should run, and the time with which both motors should run (in milliseconds).
     * <br /><br />
     * If speed is negative for either motor, that motor will run in reverse.
     * <br /><br />
     * If time is 0, the motors will run infinitely until another call to both specific motors is made, even if the Java program terminates.
     * 
     * <br /><br />An error message will display on error.<br /><br />
     * 
     * <b>Note: This method is a blocking method <u>unless</u> time = 0</b>
     * 
     * @param motor1 The first DC motor: RXTXRobot.MOTOR1 or RXTXRobot.MOTOR2
     * @param speed1 The speed that the first DC motor should run at
     * @param motor2 The second DC motor: RXTXRobot.MOTOR1 or RXTXRobot.MOTOR2
     * @param speed2 The speed that the second DC motor should run at
     * @param time The amount of time that the DC motors should run
     */
    public void runMotor(int motor1, int speed1, int motor2, int speed2, int time)
    {
        if (time < 0)
        {
            System.err.println("ERROR: runMotor was not given a time that is >=0");
            return;
        }
        if ((motor1 != RXTXRobot.MOTOR1 && motor1 != RXTXRobot.MOTOR2) || (motor2 != RXTXRobot.MOTOR1 && motor2 != RXTXRobot.MOTOR2))
        {
            System.err.println("ERROR: runMotor was not given a correct motor argument");
            return;
        }
        debug("Running two motors, motor " + motor1 + " at speed " + speed1 + " and motor " + motor2 + " at speed " + speed2 + " for time of " + time);
        sendRaw("D " + motor1 + " " + speed1 +" " + motor2 + " " + speed2 + " " + time);
        if (errorFlag == 0)
            sleep(time);
    }
}