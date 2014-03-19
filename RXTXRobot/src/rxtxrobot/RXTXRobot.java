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

/**
 * @author Chris King
 * @version 3.1.5
 */
public class RXTXRobot extends SerialCommunication
{
        /*
         * Constants - These should not change unless you know what you are
         * doing
         */

         /**
         * Version numbers to be checked against 
         * firmware versioning numbers
         */
        final private static int VERSION_MAJOR = 0;
        final private static int VERSION_MINOR = 0;
        final private static int VERSION_SUBMINOR = 0; 

        final private static boolean ONLY_ALLOW_TWO_MOTORS = true;
        /**
         * Refers to the servo motor located in SERVO1 (pin 9)
         */
        final public static int SERVO1 = 0;
        /**
         * Refers to the servo motor located in SERVO2 (pin 10)
         */
        final public static int SERVO2 = 1;
        /**
         * Refers to the servo motor located in SERVO3 (pin 11)
         */
        final public static int SERVO3 = 2;
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
         * The maximum number of digital pins that can be read from the Arduino
         * (0 &le; pins &lt; NUM_DIGITAL_PINS)
         */
        final public static int NUM_DIGITAL_PINS = 3;
        /**
         * The maximum number of analog pins that can be read from the Arduino
         * (0 &le; pins &lt; NUM_ANALOG_PINS)
         */
        final public static int NUM_ANALOG_PINS = 6;

        /*
         * Private variables
         */
        private int[] analogPinCache;
        private int[] digitalPinCache;
        private boolean[] motorsRunning =
        {
                false, false, false, false
        };
        private boolean resetOnClose;
        private boolean overrideValidation;
        private int mixerSpeed;
        private InputStream in;
        private OutputStream out;
        private SerialPort sPort;
        private CommPort cPort;
        private boolean hasEncodedMotors;
        // This is a flag to set if the communication should try again
        private boolean attemptTryAgain;
        private boolean waitForResponse;
        private boolean moveEncodedMotor;

        /**
         * Creates a new RXTXRobot object.
         *
         * This constructor creates a new RXTXRobot object.
         */
        public RXTXRobot()
        {
                super();
                analogPinCache = null;
                digitalPinCache = null;
                mixerSpeed = 30;
                resetOnClose = true;
                moveEncodedMotor = false;
                overrideValidation = false;
                attemptTryAgain = false;
                waitForResponse = false;
                hasEncodedMotors = false;
        }

        /**
         * Attempts to connect to the Arduino/XBee.
         *
         * This method attempts to make a serial connection to the Arduino/XBee
         * if the port is correct. If there is an error in connecting, then the
         * appropriate error message will be displayed. <br /><br /> This
         * function will terminate runtime if an error is discovered.
         */
        @Override
        public final void connect()
        {
                this.getOutStream().println("Connecting to robot, please wait...\n");
                if ("".equals(getPort()))
                {
                        error("No port was specified to connect to!\n" + SerialCommunication.displayPossiblePorts(), "RXTXRobot", "connect", true);
                        System.exit(1);
                }
                if (isConnected())
                {
                        error("Robot is already connected!", "RXTXRobot", "connect");
                        return;
                }
                try
                {
                        java.io.PrintStream originalStream = System.out;
                        System.setOut(new java.io.PrintStream(new java.io.OutputStream()
                        {
                                @Override
                                public void write(int i) throws IOException
                                {
                                        // Do nothing to silence the mismatch warning.
                                }
                        }));
                        CommPortIdentifier pIdent = CommPortIdentifier.getPortIdentifier(getPort());
                        System.setOut(originalStream);
                        if (pIdent.isCurrentlyOwned())
                        {
                                error("Arduino port (" + getPort() + ") is currently owned by " + pIdent.getCurrentOwner() + "!\n" + SerialCommunication.displayPossiblePorts(), "RXTXRobot", "connect", true);
                                System.exit(1);
                        }
                        cPort = pIdent.open("RXTXRobot", 2000);
                        if (cPort instanceof SerialPort)
                        {
                                sPort = (SerialPort) cPort;
                                sPort.setSerialPortParams(getBaudRate(), SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                                debug("Resetting robot...");
                                sleep(500);
                                in = sPort.getInputStream();
                                out = sPort.getOutputStream();
                                sleep(2500);
                                this.getOutStream().println("Connected!\n");
                        }
                }
                catch (NoSuchPortException e)
                {
                        error("Invalid port (NoSuchPortException).  Check to make sure the correct port is set at the object's initialization.\n" + SerialCommunication.displayPossiblePorts(), "RXTXRobot", "connect", true);
                        if (getVerbose())
                        {
                                this.getErrStream().println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
                                e.printStackTrace(this.getErrStream());
                        }
                        System.exit(1);
                }
                catch (PortInUseException e)
                {
                        error("Port is already being used by a different application (PortInUseException).  Did you stop a previously running instance of this program?\n" + SerialCommunication.displayPossiblePorts(), "RXTXRobot", "connect", true);
                        if (getVerbose())
                        {
                                this.getErrStream().println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
                                e.printStackTrace(this.getErrStream());
                        }
                        System.exit(1);
                }
                catch (UnsupportedCommOperationException e)
                {
                        error("Comm Operation is unsupported (UnsupportedCommOperationException).  This should never happen.  If you see this, ask a TA for assistance.", "RXTXRobot", "connect", true);
                        if (getVerbose())
                        {
                                this.getErrStream().println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
                                e.printStackTrace(this.getErrStream());
                        }
                        System.exit(1);
                }
                catch (IOException e)
                {
                        error("Could not assign Input and Output streams (IOException).  You may be calling the \"close()\" method before this.  Make sure you only call \"close()\" at the very end of your program!", "RXTXRobot", "connect", true);
                        if (getVerbose())
                        {
                                this.getErrStream().println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
                                e.printStackTrace(this.getErrStream());
                        }
                        System.exit(1);
                }
                catch (Exception e)
                {
                        error("A generic error occurred: " + e.getMessage(), "RXTXRobot", "connect", true);
                        if (getVerbose())
                        {
                                this.getErrStream().println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
                                e.printStackTrace(this.getErrStream());
                        }
                        System.exit(1);
                }

                String response = this.sendRaw("n",0);
                String[] arr = response.split("\\s+");
                if (arr.length != 4)
                {
                    error("Incorrect response from Arduino (Invalid length)!", "RXTXRobot", "versionNumber"); 
                    debug("Version Response: " + response); 
                    return; 
                }
                try
                {
                    int firmwareVMajor = Integer.parseInt(arr[1]); 
                    int firmwareVMinor = Integer.parseInt(arr[2]);
                    int firmwareVSubminor = Integer.parseInt(arr[3]); 
                    if(firmwareVMajor > VERSION_MAJOR)
                    {
                       System.out.println("Version number mismatch. Please update API");
                    } 
                    else if (firmwareVMinor > VERSION_MINOR)
                    {
                        System.out.println("Version number mismatch. API update suggested"); 
                    }
                    else if (firmwareVSubminor > VERSION_SUBMINOR)
                    {
                        System.out.println("Subminor firmware update, no action necessary"); 
                    }
                    System.out.println("\nFirmware version: " + firmwareVMajor + "." + firmwareVMinor + "." + firmwareVSubminor +
                        "\nAPI version: " + VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_SUBMINOR); 
                }
                catch(Exception e)
                {
                    error("Incorrect response from Arduino (Invalid datatype)!","RXTXRobot","versionNumber");
                    debug("Version Response: " + response);
                }


        }

        /**
         * Checks if the RXTXRobot object is connected to the Arduino/XBee.
         *
         * Returns true if the RXTXRobot object is successfully connected to the
         * Arduino/XBee. Returns false otherwise.
         *
         * @return true/false value that specifies if the RXTXRobot object is
         * connected to the Arduino/XBee.
         */
        @Override
        public final boolean isConnected()
        {
                return sPort != null && cPort != null && in != null && out != null;
        }

        /**
         * Closes the connection to the Arduino/XBee.
         *
         * This method closes the serial connection to the Arduino/XBee. It
         * deletes the mutual exclusion lock file, which is important, so this
         * should be done before the program is terminated.
         */
        @Override
        public final void close()
        {
                sleep(300);
                this.getOutStream().println("");
                this.getOutStream().println("Closing robot connection...");
                if (getResetOnClose())
                {
                        this.getOutStream().println("Resetting servos and motors...");
                        this.moveAllServos(90, 90, 90);
                        this.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
                        this.runMotor(RXTXRobot.MOTOR3, 0, RXTXRobot.MOTOR4, 0, 0);
                }
                if (sPort != null)
                        sPort.close();
                if (cPort != null)
                        cPort.close();
                in = null;
                out = null;
                this.getOutStream().println("Connection closed!");
        }

        /**
         * Sends a string to the Arduino to be executed.
         *
         * If a serial connection is present, then it sends the String to the
         * Arduino to be executed. If verbose is on, then the response from the
         * Arduino is displayed.
         *
         * @param str The command to send to the Arduino
         */
        public String sendRaw(String str)
        {
                return sendRaw(str, 100);
        }

        /**
         * Sends a string to the Arduino to be executed with a specified delay.
         *
         * If a serial connection is present, then it sends the String to the
         * Arduino to be executed. If verbose is on, then the response from the
         * Arduino is displayed.
         *
         * @param str The command to send to the Arduino
         * @param sleep The number of milliseconds to wait for a response
         */
        public String sendRaw(String str, int sleep)
        {
                debug("Sending command: " + str);
                if (!isConnected())
                {
                        error("Cannot send command because the robot is not connected.", "RXTXRobot", "sendRaw");
                        return "";
                }
                try
                {
                        byte[] buffer;
                        int retries = 4; // Number of retries
                        do
                        {
                                out.write((str + "\r\n").getBytes());
                                buffer = new byte[1024];
                                sleep(sleep);
                                --retries;
                                if (!waitForResponse)
                                {
                                        if (in.available() == 0 && attemptTryAgain && retries != 0)
                                        {
                                                debug("No response from the Arduino....Trying " + retries + " more times");
                                        }
                                        if (retries == 0)
                                        {
                                                error("There was no response from the Arduino", "RXTXRobot", "sendRaw");
                                        }
                                }
                        }
                        while (in.available() == 0 && attemptTryAgain && !waitForResponse && retries != 0);
                        int bytesRead = 0;
                        String t = "";
                        do
                        {
                            bytesRead += in.read(buffer, bytesRead, 1024-bytesRead);
                            t = new String(buffer).trim();
                        } while (this.moveEncodedMotor && !str.equals(t));
                        String ret = (new String(buffer)).trim();
                        debug("Received " + bytesRead + " bytes from the Arduino: " + ret);
                        return ret;
                }
                catch (IOException e)
                {
                        error("Could not read or use Input or Output streams (IOException)", "RXTXRobot", "sendRaw");
                        if (getVerbose())
                        {
                                this.getErrStream().println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
                                e.printStackTrace(this.getErrStream());
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
                for (int x = 0; x < analogPinCache.length; ++x)
                        analogPinCache[x] = -1;
                if (!isConnected())
                {
                        error("Robot is not connected!", "RXTXRobot", "refreshAnalogPins");
                        return;
                }
                try
                {
                        attemptTryAgain = true;
                        String[] split = sendRaw("r a").split("\\s+");
                        attemptTryAgain = false;
                        if (split.length <= 1)
                        {
                                error("No response was received from the Arduino.", "RXTXRobot", "refreshAnalogPins");
                                return;
                        }
                        if (split.length - 1 != RXTXRobot.NUM_ANALOG_PINS)
                        {
                                error("Incorrect length returned: " + split.length + ".", "RXTXRobot", "refreshAnalogPins");
                                if (getVerbose())
                                        for (int x = 0; x < split.length; ++x)
                                                this.getErrStream().println("[" + x + "] = " + split[x]);
                                return;
                        }
                        for (int x = 1; x < split.length; ++x)
                                analogPinCache[x - 1] = Integer.parseInt(split[x]);
                }
                catch (NumberFormatException e)
                {
                        error("Returned string could not be parsed into Integers.", "RXTXRobot", "refreshAnalogPins");
                }
                catch (Exception e)
                {
                        error("A generic error occurred.", "RXTXRobot", "refreshAnalogPins");
                        if (getVerbose())
                        {
                                this.getErrStream().println("Stacktrace: ");
                                e.printStackTrace(this.getErrStream());
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
                for (int x = 0; x < digitalPinCache.length; ++x)
                        digitalPinCache[x] = -1;
                if (!isConnected())
                {
                        error("Robot is not connected!", "RXTXRobot", "refreshDigitalPins");
                        return;
                }
                try
                {
                        attemptTryAgain = true;
                        String[] split = sendRaw("r d").split("\\s+");
                        attemptTryAgain = false;
                        if (split.length <= 1)
                        {
                                error("No response was received from the Arduino.", "RXTXRobot", "refreshDigitalPins");
                                return;
                        }
                        if (split.length - 1 != RXTXRobot.NUM_DIGITAL_PINS)
                        {
                                error("Incorrect length returned: " + split.length + ".", "RXTXRobot", "refreshDigitalPins");
                                if (getVerbose())
                                        for (int x = 0; x < split.length; ++x)
                                                this.getErrStream().println("[" + x + "] = " + split[x]);
                                return;
                        }
                        for (int x = 1; x < split.length; ++x)
                                digitalPinCache[x - 1] = Integer.parseInt(split[x]);
                }
                catch (NumberFormatException e)
                {
                        error("Returned string could not be parsed into Integers.", "RXTXRobot", "refreshDigitalPins");
                }
                catch (Exception e)
                {
                        error("A generic error occurred.", "RXTXRobot", "refreshDigitalPins");
                        if (getVerbose())
                        {
                                this.getErrStream().println("Stacktrace: ");
                                e.printStackTrace(this.getErrStream());
                        }
                }
        }

        /**
         * Returns an AnalogPin object for the specified pin.
         *
         * This will get the value of the pin since the last time
         * {@link #refreshAnalogPins() refreshAnalogPins()} was called.
         *
         * @param x The number of the pin: 0 &lt; x &lt;
         * {@link #NUM_ANALOG_PINS NUM_ANALOG_PINS}
         * @return AnalogPin object of the specified pin, or null if error.
         */
        public AnalogPin getAnalogPin(int x)
        {
                if (analogPinCache == null)
                        this.refreshAnalogPins();
                if (!getOverrideValidation() && (x >= analogPinCache.length || x < 0))
                {
                        error("Analog pin " + x + " doesn't exist.", "RXTXRobot", "getAnalogPin");
                        return null;
                }
                return new AnalogPin(x, analogPinCache[x]);
        }

        /**
         * Returns a DigitalPin object for the specified pin.
         *
         * This will get the value of the pin since the last time
         * {@link #refreshDigitalPins() refreshDigitalPins()} was called.
         *
         * @param x The number of the pin: 0 &lt; x &lt;
         * {@link #NUM_DIGITAL_PINS NUM_DIGITAL_PINS}
         * @return DigitalPin object of the specified pin, or null if error.
         */
        public DigitalPin getDigitalPin(int x)
        {
                final int[][] mapping = { {4, 0},
			                              {11, 1},
			                              {12, 2}};
                if (digitalPinCache == null)
                        this.refreshDigitalPins();
                for (int y = 0; y < mapping.length; ++y)
                {
                        if (mapping[y][0] == x)
                                return new DigitalPin(x, digitalPinCache[mapping[y][1]]);
                }
                error("Digital pin " + x + " doesn't exist.", "RXTXRobot", "getDigitalPin");
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
                if (!isConnected())
                {
                        error("Robot is not connected!", "RXTXRobot", "getTemperature");
                        return -1;
                }
                try
                {
                        this.waitForResponse = true;
                        String[] split = sendRaw("r t",1000).split("\\s+");
                        this.waitForResponse = false;
                        if (split.length <= 1)
                        {
                                error("No response was received from the Arduino.", "RXTXRobot", "getTemperature");
                                return -1;
                        }
                        if (split.length - 1 != 1)
                        {
                                error("Incorrect length returned: " + split.length + ".", "RXTXRobot", "getTemperature");
                                if (getVerbose())
                                        for (int x = 0; x < split.length; ++x)
                                                this.getErrStream().println("[" + x + "] = " + split[x]);
                                return -1;
                        }
                        return Integer.parseInt(split[1]);
                }
                catch (NumberFormatException e)
                {
                        error("Returned string could not be parsed into an Integer.", "RXTXRobot", "getTemperature");
                }
                catch (Exception e)
                {
                        error("A generic error occurred.", "RXTXRobot", "getTemperature");
                        if (getVerbose())
                        {
                                this.getErrStream().println("Stacktrace: ");
                                e.printStackTrace(this.getErrStream());
                        }
                }
                return -1;
        }

        /**
         * Gets the result from the Ping sensor (must be on digital pin 13).
         *
         * The Ping sensor must be on digital pin 13 and this method returns the distance in centimeters.
         * @return The distance from an object in centimeters
         */
        public int getPing()
        {
                if (!isConnected())
                {
                        error("Robot isn't connected!", "RXTXRobot", "getPing");
                        return -1;
                }
                this.attemptTryAgain = true;
                String response = this.sendRaw("q",200);
                String[] arr = response.split("\\s+");
                this.attemptTryAgain = false;
                if (arr.length != 2)
                {
                        error("Incorrect response from Arduino (Invalid length)!", "RXTXRobot", "getPing");
                        debug("Ping Response: " + response);
                        return -1;
                }
                try
                {
                        return Integer.parseInt(arr[1]);
                }
                catch (Exception e)
                {
                        error("Incorrect response from Arduino (Invalid datatype)!", "RXTXRobot", "getPing");
                        debug("Ping Response: " + response);
                }
                return -1;
        }
        
        /**
         * Gets the result from the conductivity sensor.
         * 
         * The conductivity sensor requires a total of 4 pins: 2 digital pins on
         * digital pins 11 and 12, and 2 analog pins on analog pins 4 and 5.
         * @return The conductivity measurement
         */
        public int getConductivity()
        {
                if (!isConnected())
                {
                        error("Robot isn't connected!", "RXTXRobot", "getConductivity");
                        return -1;
                }
                
                this.attemptTryAgain = true;
                String response = this.sendRaw("c", 3000);
                String[] arr = response.split("\\s+");
                this.attemptTryAgain = false;
                
                if (arr.length != 2)
                {
                        error("Incorrect response from Arduino (Invalid length)!", "RXTXRobot", "getConductivity");
                        debug("Conductivity Response: " + response);
                        return -1;
                }
                
                try
                {
                        return Integer.parseInt(arr[1]);
                }
                catch (Exception e)
                {
                        error("Incorrect response from Arduino (Invalid datatype)!", "RXTXRobot", "getConductivity");
                        debug("Conductivity Response: " + response);
                }
                return -1;
        }
        /**
         * Moves the specified servo to the specified angular position.
         *
         * Accepts either {@link #SERVO1 RXTXRobot.SERVO1} or
         * {@link #SERVO2 RXTXRobot.SERVO2} and an angular position between 0
         * and 180 inclusive. <br /><br /> The servo starts at 90 degrees, so a
         * number &lt;90 will turn it one way, and a number &gt;90 will turn it
         * the other way. <br /><br /> An error message will be displayed on
         * error.
         *
         * @param servo The servo motor that you would like to move:
         * {@link #SERVO1 RXTXRobot.SERVO1}, {@link #SERVO2 RXTXRobot.SERVO2},
         * or {@link #SERVO3 RXTXRobot.SERVO3}.
         * @param position The position (in degrees) where you want the servo to
         * turn to: 0 &lt; position &lt; 180.
         */
        public void moveServo(int servo, int position)
        {
                if (!isConnected())
                {
                        error("Robot is not connected!", "RXTXRobot", "moveServo");
                        return;
                }
                if (!getOverrideValidation() && servo != RXTXRobot.SERVO1 && servo != RXTXRobot.SERVO2 && servo != RXTXRobot.SERVO3)
                {
                        error("Invalid servo argument.", "RXTXRobot", "moveServo");
                        return;
                }
                debug("Moving servo " + servo + " to position " + position);
                if (!getOverrideValidation() && (position < 0 || position > 180))
                        error("Position must be >=0 and <=180.  You supplied " + position + ", which is invalid.", "RXTXRobot", "moveServo");
                else
                        sendRaw("v " + servo + " " + position);
        }

        /**
         * Moves both servos simultaneously to the desired positions.
         *
         * Accepts two angular positions between 0 and 180 inclusive and moves
         * the servo motors to the corresponding angular position.
         * {@link #SERVO1 SERVO1} moves {@code pos1} degrees,
         * {@link #SERVO2 SERVO2} moves {@code pos2} degrees, and
         * {@link #SERVO3 SERVO3} moves {@code pos3} degrees. <br /><br /> The
         * servos start at 90 degrees, so a number &lt; 90 will turn it one way,
         * and a number &gt; 90 will turn it the other way. <br /><br /> An
         * error message will be displayed on error.
         *
         * @param pos1 The angular position of RXTXRobot.SERVO1
         * @param pos2 The angular position of RXTXRobot.SERVO2
         * @param pos3 The angular position of RXTXRobot.SERVO3
         */
        public void moveAllServos(int pos1, int pos2, int pos3)
        {
                if (!isConnected())
                {
                        error("Robot is not connected!", "RXTXRobot", "moveBothServos");
                        return;
                }
                debug("Moving servos to positions " + pos1 + ", " + pos2 + ", and " + pos3);
                if (!getOverrideValidation() && (pos1 < 0 || pos1 > 180 || pos2 < 0 || pos2 > 180 || pos3 < 0 || pos3 > 180))
                        error("Positions must be >=0 and <=180.  You supplied " + pos1 + " and " + pos2 + ".  One or more are invalid.", "RXTXRobot", "moveBothServos");
                else
                        sendRaw("V " + pos1 + " " + pos2 + " " + pos3);
        }

        /**
         * Runs a DC motor at a specific speed for a specific time (Potential
         * blocking method).
         *
         * Accepts a DC motor, either {@link #MOTOR1 RXTXRobot.MOTOR1},
         * {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}, the speed that the motor should
         * run at (-500 - 500), and the time with which the motor should run (in
         * milliseconds). <br /><br /> If speed is negative, the motor will run
         * in reverse. <br /><br /> If time is 0, the motor will run infinitely
         * until another call to that motor is made, even if the Java program
         * terminates. <br /><br /> An error message will display on error. <br
         * /><br /> Note: This method is a blocking method unless time = 0
         *
         * @param motor The DC motor you want to run:
         * {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}
         * @param speed The speed that the motor should run at (-500 - 500)
         * @param time The number of milliseconds the motor should run (0 for
         * infinite) (may not be above 30,000 (30 seconds))
         */
        public void runMotor(int motor, int speed, int time)
        {
                if (!isConnected())
                {
                        error("Robot is not connected!", "RXTXRobot", "runMotor");
                        return;
                }
                if (!getOverrideValidation() && (speed < -500 || speed > 500))
                {
                        error("You must give the motors a speed between -500 and 500 (inclusive).", "RXTXRobot", "runMotor");
                        return;
                }
                if (!getOverrideValidation() && RXTXRobot.ONLY_ALLOW_TWO_MOTORS)
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
                if (!getOverrideValidation() && (time < 0 || time > 30000))
                {
                        error("runMotor not given a time that is 0 <= time <= 30000.", "RXTXRobot", "runMotor");
                        return;
                }
                if (!getOverrideValidation() && (motor < RXTXRobot.MOTOR1 || motor > RXTXRobot.MOTOR4))
                {
                        error("runMotor was not given a correct motor argument.", "RXTXRobot", "runMotor");
                        return;
                }
                debug("Running motor " + motor + " at speed " + speed + " for time of " + time);
                if (!"".equals(sendRaw("d " + motor + " " + speed + " " + time)))
                        sleep(time);
                if (time != 0)
                        motorsRunning[motor] = false;
        }

        /**
         * Runs both DC motors at different speeds for the same amount of time.
         * (Potential blocking method).
         *
         * Accepts a DC motor, either {@link #MOTOR1 RXTXRobot.MOTOR1},
         * {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}, the speed in which that motor
         * should run (-500 - 500), accepts another DC motor, the speed in which
         * that motor should run, and the time with which both motors should run
         * (in milliseconds). <br /><br /> If speed is negative for either
         * motor, that motor will run in reverse. <br /><br /> If time is 0, the
         * motors will run infinitely until another call to both specific motors
         * is made, even if the Java program terminates. <br /><br /> An error
         * message will display on error. <br /><br /> Note: This method is a
         * blocking method unless time = 0
         *
         * @param motor1 The first DC motor:
         * {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}
         * @param speed1 The speed that the first DC motor should run at
         * @param motor2 The second DC motor:
         * {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}
         * @param speed2 The speed that the second DC motor should run at
         * @param time The amount of time that the DC motors should run (may not
         * be more than 30,000 (30 seconds).
         */
        public void runMotor(int motor1, int speed1, int motor2, int speed2, int time)
        {
                if (!isConnected())
                {
                        error("Robot is not connected!", "RXTXRobot", "runMotor");
                        return;
                }
                if (!getOverrideValidation() && (speed1 < -500 || speed1 > 500 || speed2 < -500 || speed2 > 500))
                {
                        error("You must give the motors a speed between -500 and 500 (inclusive).", "RXTXRobot", "runMotor");
                        return;
                }
                if (!getOverrideValidation() && RXTXRobot.ONLY_ALLOW_TWO_MOTORS)
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
                if (!getOverrideValidation() && (time < 0 || time > 30000))
                {
                        error("runMotor was not given a time that is 0 <= time <= 30000.", "RXTXRobot", "runMotor");
                        return;
                }
                if (!getOverrideValidation() && ((motor1 < RXTXRobot.MOTOR1 || motor1 > RXTXRobot.MOTOR4) || (motor2 < RXTXRobot.MOTOR1 || motor2 > RXTXRobot.MOTOR4)))
                {
                        error("runMotor was not given a correct motor argument.", "RXTXRobot", "runMotor");
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
         * Runs four DC motors at different speeds for the same amount of time.
         * (Potential blocking method) Accepts DC motors, either {@link #MOTOR1 RXTXRobot.MOTOR1},
         * {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * {@link #MOTOR4 RXTXRobot.MOTOR4}, the speed in which those motor
         * should run (-500 - 500), accepts another DC motor, the speed in which
         * that motor should run, etc, and the time with which both motors
         * should run (in milliseconds). <br /><br /> If speed is negative for
         * any motor, that motor will run in reverse. <br /><br /> If time is 0,
         * the motors will run infinitely until another call to both specific
         * motors is made, even if the Java program terminates. <br /><br /> An
         * error message will display on error. <br /><br /> Note: This method
         * is a blocking method unless time = 0
         *
         * @param motor1 The first DC motor:
         * {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}
         * @param speed1 The speed that the first DC motor should run at
         * @param motor2 The second DC motor:
         * {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}
         * @param speed2 The speed that the second DC motor should run at
         * @param motor3 The third DC motor:
         * {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}
         * @param speed3 The speed that the third DC motor should run at
         * @param motor4 The fourth DC motor:
         * {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}
         * @param speed4 The speed that the fourth DC motor should run at
         * @param time The amount of time that the DC motors should run
         */
        protected void runMotor(int motor1, int speed1, int motor2, int speed2, int motor3, int speed3, int motor4, int speed4, int time)
        {
                if (!isConnected())
                {
                        error("Robot is not connected!", "RXTXRobot", "runMotor");
                        return;
                }
                if (!getOverrideValidation() && (speed1 < -255 || speed1 > 255 || speed2 < -255 || speed2 > 255 || speed3 < -255 || speed3 > 255 || speed4 < -255 || speed4 > 255))
                {
                        error("You must give the motors a speed between -255 and 255 (inclusive).", "RXTXRobot", "runMotor");
                        return;
                }
                if (!getOverrideValidation() && RXTXRobot.ONLY_ALLOW_TWO_MOTORS)
                {
                        error("You may only run two DC motors at a time, so you cannot use this method!", "RXTXRobot", "runMotor");
                        return;
                }
                if (!getOverrideValidation() && time < 0)
                {
                        error("runMotor was not given a time that is >=0.", "RXTXRobot", "runMotor");
                        return;
                }
                if (!getOverrideValidation() && ((motor1 < 0 || motor1 > 3) || (motor2 < 0 || motor2 > 3) || (motor3 < 0 || motor3 > 3) || (motor4 < 0 || motor4 > 3)))
                {
                        error("runMotor was not given a correct motor argument.", "RXTXRobot", "runMotor");
                        return;
                }
                debug("Running four motors, motor " + motor1 + " at speed " + speed1 + " and motor " + motor2 + " at speed " + speed2 + " and motor " + motor3 + " at speed " + speed3 + " and motor " + motor4 + " at speed " + speed4 + " for time of " + time);
                if (!"".equals(sendRaw("F " + motor1 + " " + speed1 + " " + motor2 + " " + speed2 + " " + motor3 + " " + speed3 + " " + motor4 + " " + speed4 + " " + time)))
                        sleep(time);
        }

        /**
         * Runs a DC encoded motor at a specific speed for a specific distance
         * (Blocking Method).
         *
         * Accepts a DC motor, either {@link #MOTOR1 RXTXRobot.MOTOR1} or
         * {@link #MOTOR2 RXTXRobot.MOTOR2}, the speed that the motor should
         * run at (-500 - 500), and the tick to move to. <br /><br /> If speed
         * is negative, the motor will run in reverse. <br /><br /> An error
         * message will display on error. <br /><br /> Note: This method is a
         * blocking method
         *
         * @param motor The DC motor you want to run:
         * {@link #MOTOR1 RXTXRobot.MOTOR1} or {@link #MOTOR2 RXTXRobot.MOTOR2}
         * @param speed The speed that the motor should run at (-500 - 500)
         * @param ticks The tick that the motor should move
         */
        public void runEncodedMotor(int motor, int speed, int ticks)
        {
                if (!isConnected())
                {
                        error("Robot is not connected!", "RXTXRobot", "runEncodedMotor");
                        return;
                }
                if (!getOverrideValidation() && (speed < -500 || speed > 500))
                {
                        error("You must give the motors a speed between -500 and 500 (inclusive).", "RXTXRobot", "runEncodedMotor");
                        return;
                }
                if (!getOverrideValidation() && (motor < RXTXRobot.MOTOR1 || motor > RXTXRobot.MOTOR2))
                {
                        error("runEncodedMotor was not given a correct motor argument.", "RXTXRobot", "runEncodedMotor");
                        return;
                }
                if (!getOverrideValidation() && (ticks <= 0))
                {
                        error("runEncodedMotor was not given a positive tick argument.", "RXTXRobot", "runEncodedMotor");
                        return;
                }
                if (!getOverrideValidation() && RXTXRobot.ONLY_ALLOW_TWO_MOTORS)
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
                debug("Running encoded motor " + motor + " to tick " + ticks + " at speed " + speed);
                this.moveEncodedMotor = true;
                if (!"".equals(sendRaw("e " + motor + " " + speed + " " + ticks)))
                {
                    debug("Done running encoded motor");
                }
                this.moveEncodedMotor = false;
                motorsRunning[motor] = false;
                sleep(1000);
        }

        /**
         * Runs a DC encoded motor at a specific speed for a specific distance
         * (Blocking Method).
         *
         * Accepts a DC motor, either {@link #MOTOR1 RXTXRobot.MOTOR1} or
         * {@link #MOTOR2 RXTXRobot.MOTOR2}, the speed in which that motor
         * should run (-500 - 500), accepts another DC motor, the speed in which
         * that motor should run, and the time with which both motors should run
         * (in milliseconds). <br /><br /> If speed is negative for either
         * motor, that motor will run in reverse. <br /><br /> An error message
         * will display on error. <br /><br /> Note: This method is a blocking
         * method unless time = 0
         *
         * @param motor1 The first DC motor:
         * {@link #MOTOR1 RXTXRobot.MOTOR1} or {@link #MOTOR2 RXTXRobot.MOTOR2}
         * @param speed1 The speed that the first DC motor should run at
         * @param tick1 The tick that the first DC motor should move to
         * @param motor2 The second DC motor:
         * {@link #MOTOR1 RXTXRobot.MOTOR1} or {@link #MOTOR2 RXTXRobot.MOTOR2}
         * @param speed2 The speed that the second DC motor should run at
         * @param tick2 The tick that the second DC motor should move
         */
        public void runEncodedMotor(int motor1, int speed1, int tick1, int motor2, int speed2, int tick2)
        {
                if (!isConnected())
                {
                        error("Robot is not connected!", "RXTXRobot", "runEncodedMotor");
                        return;
                }
//              
                if (!getOverrideValidation() && (speed1 < -500 || speed1 > 500 || speed2 < -500 || speed2 > 500))
                {
                        error("You must give the motors a speed between -500 and 500 (inclusive).", "RXTXRobot", "runEncodedMotor");
                        return;
                }
                if (!getOverrideValidation() && RXTXRobot.ONLY_ALLOW_TWO_MOTORS)
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
                if (!getOverrideValidation() && (tick1 <= 0 || tick2 <= 0))
                {
                        error("runEncodedMotor was not given a tick that is positive", "RXTXRobot", "runEncodedMotor");
                        return;
                }
                if (!getOverrideValidation() && ((motor1 < RXTXRobot.MOTOR1 || motor1 > RXTXRobot.MOTOR2) || (motor2 < RXTXRobot.MOTOR2 || motor2 > RXTXRobot.MOTOR2)))
                {
                        error("runEncodedMotor was not given a correct motor argument.", "RXTXRobot", "runEncodedMotor");
                }
                debug("Running two motors, motor " + motor1 + " at speed " + speed1 + " for " + tick1 + " ticks and motor " + motor2 + " at speed " + speed2 + " for " + tick2 + " ticks");
                this.moveEncodedMotor = true;
                if (!"".equals(sendRaw("E " + motor1 + " " + speed1 + " " + tick1 + " " + motor2 + " " + speed2 + " " + tick2)))
                {
                    debug("Done running 2 encoded motors");
                }
                this.moveEncodedMotor = false;
                motorsRunning[motor1] = false;
                motorsRunning[motor2] = false;
                sleep(1000);
        }

        /**
         * Gets the net number of ticks that a motor has turned.
         *
         * This method returns the number of ticks that a motor has moved since it was last reset.
         * This includes all motion, including distance traveled using {@link #runEncodedMotor(int, int, int) runEncodedMotor} 
         * and {@link #runMotor(int, int, int) runMotor}. Running a motor with a 
         * positive speed in either case increases its tick count. Running a motor
         * with a negative speed decreases its tick count, which means the current tick
         * count may be negative. To reset the encoder tick position back to 0,
         * use the {@link #resetEncodedMotorPosition(int) resetEncodedMotorPosition} method.
         * @param motor The motor number to get the ticks from. Can be either
         * {@link #MOTOR1 RXTXRobot.MOTOR1} or {@link #MOTOR2 RXTXRobot.MOTOR2}
         * @return Integer representing the current tick count of the encoder on the motor.
         */
        public int getEncodedMotorPosition(int motor)
        {
                if (!isConnected())
                {
                        error("Robot is not connected!", "RXTXRobot", "getEncodedMotorPosition");
                        return -1;
                }
                if (!getOverrideValidation() && (motor < RXTXRobot.MOTOR1 || motor > RXTXRobot.MOTOR2))
                {
                        error("getEncodedMotorPosition was not given a correct motor argument", "RXTXRobot", "getEncodedMotorPosition");
                        return -1;
                }
                debug("Checking tick position for encoder " + motor);
                this.attemptTryAgain = true;
                String[] split = sendRaw("p " + motor).split("\\s+");
                this.attemptTryAgain = false;
                if (split.length != 3) 
                {
                        error("Incorrect length returned: " + split.length, "RXTXRobot", "getEncodedMotorPosition");
                        return -1;
                }
                return Integer.parseInt(split[2]);
        }

        /**
         * Resets the position of a motor to 0.
         *
         * This method resets the position of a motor back to 0 so distances can begin to be measured.
         * @param motor The motor number to reset the ticks of. Can be either
         * {@link #MOTOR1 RXTXRobot.MOTOR1} or {@link #MOTOR2 RXTXRobot.MOTOR2}
         */
        public void resetEncodedMotorPosition(int motor)
        {
                if (!isConnected())
                {
                        error("Robot is not connected!", "RXTXRobot", "resetEncodedMotorPosition");
                        return;
                }
                if (!getOverrideValidation() && (motor < RXTXRobot.MOTOR1 || motor > RXTXRobot.MOTOR2))
                {
                        error("resetEncodedMotorPosition was not given a correct motor argument", "RXTXRobot", "resetEncodedMotorPosition");
                        return;
                }
                if (!"".equals(sendRaw("z " + motor))) 
                {
                        debug("Done resetting the encoder tick count");
                }
                else
                {
                        error("Empty response from the arduino");
                }
        }

        /*
         * This method just checks to make sure that only two DC motors are
         * running
         */
        private boolean checkRunningMotors()
        {
                if (getOverrideValidation())
                        return true;
                int num = 0;
                for (int x = 0; x < motorsRunning.length; ++x)
                        if (motorsRunning[x])
                                ++num;
                if (num > 2)
                {
                        error("You may not run more than two motors at any given time!", "RXTXRobot", "checkRunningMotors");
                        return false;
                }
                return true;
        }

        /**
         * Runs the small, mixing motor for a specific time. (Potential blocking
         * method)
         *
         * Accepts a motor location ({@link #MOTOR1 RXTXRobot.MOTOR1},
         * {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}), and the time with which the
         * motor should run (in milliseconds). <br /><br /> If time is 0, the
         * motor will run infinitely until a call to
         * {@link #stopMixer(int) stopMixer}, even if the Java program
         * terminates. <br /><br /> An error message will display on error. <br
         * /><br /> Note: This method is a blocking method unless time = 0
         *
         * @param motor The motor that the mixer is on:
         * {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}
         * @param time The number of milliseconds the motor should run (0 for
         * infinite)
         */
        public void runMixer(int motor, int time)
        {
                if (!isConnected())
                {
                        error("Robot is not connected!", "RXTXRobot", "runMixer");
                        return;
                }
                if (!getOverrideValidation() && (motor < RXTXRobot.MOTOR1 || motor > RXTXRobot.MOTOR4))
                {
                        error("You must supply a valid motor port: RXTXRobot.MOTOR1, MOTOR2, MOTOR3, or MOTOR4.", "RXTXRobot", "runMixer");
                        return;
                }
                if (!getOverrideValidation() && time < 0)
                {
                        error("You must supply a positive time.", "RXTXRobot", "runMixer");
                        return;
                }
                debug("Running mixer on port " + motor + " at speed " + getMixerSpeed() + " for time of " + time);
                if (!"".equals(sendRaw("d " + motor + " " + getMixerSpeed() + " " + time)))
                        sleep(time);
        }

        /**
         * Stops the small, mixing motor if it is currently running.
         *
         * This method should be called if {@link #runMixer(int,int) runMixer}
         * was called with a time of 0. This method will stop the mixer.
         *
         * @param motor The motor that the mixer is on:
         * {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}
         */
        public void stopMixer(int motor)
        {
                if (!isConnected())
                {
                        error("Robot is not connected!", "RXTXRobot", "stopMixer");
                        return;
                }
                if (!getOverrideValidation() && (motor < RXTXRobot.MOTOR1 || motor > RXTXRobot.MOTOR4))
                {
                        error("You must supply a valid motor port: RXTXRobot.MOTOR1, MOTOR2, MOTOR3, or MOTOR4.", "RXTXRobot", "stopMixer");
                        return;
                }
                debug("Stopping mixer on port " + motor);
                sendRaw("d " + motor + " 0 0");
        }

        /**
         * Sets the speed for the mixer.
         *
         * This method sets the speed for the mixer. Default is 30, but the
         * value must be between 0 and 255.
         *
         * @param speed Integer representing the speed (0 - 255)
         */
        public void setMixerSpeed(int speed)
        {
                if (!getOverrideValidation() && speed > 255)
                {
                        error("The speed supplied (" + speed + ") is > 255.  Resetting it to 255.", "RXTXRobot", "setMixerSpeed");
                        speed = 255;
                }
                if (!getOverrideValidation() && speed < 0)
                {
                        error("The speed supplied (" + speed + ") is < 0.  Resetting it to 0.", "RXTXRobot", "setMixerSpeed");
                        speed = 0;
                }
                this.mixerSpeed = speed;
        }

        /**
         * Gets the speed for the mixer.
         *
         * @return Integer representing the speed.
         */
        public int getMixerSpeed()
        {
                return this.mixerSpeed;
        }

        /**
         * Sets whether to reset the Servos and Motors when the connection is
         * closed.
         *
         * Default is true.
         *
         * @param r Boolean representing whether to reset motors or not.
         */
        public void setResetOnClose(boolean r)
        {
                this.resetOnClose = r;
        }

        /**
         * Gets whether the robot will reset the motors when the connection is
         * closed.
         *
         * @return Boolean representing if the robot will reset the motors when
         * the connection is closed.
         */
        public boolean getResetOnClose()
        {
                return this.resetOnClose;
        }

        /**
         * <b>DANGEROUS:</b> Sets whether to override the validation of inputs
         * or not.
         *
         * You should not set this to true unless you know what you are doing!
         * Default is false.
         *
         * @param o Boolean representing whether to override validation checks.
         */
        public void setOverrideValidation(boolean o)
        {
                if (o)
                {
                        this.getErrStream().println("**********WARNING**********");
                        this.getErrStream().println("Validation has been overridden!  You are now responsible for the values you send to the device");
                }
                this.overrideValidation = o;
        }

        /**
         * Gets whether to override the validation of inputs.
         *
         * @return Boolean representing if the validation of inputs will be
         * overridden
         */
        public boolean getOverrideValidation()
        {
                return this.overrideValidation;
        }

        /**
         * Sets whether the robot is using motors with encoders or not.
         *
         * Default is true.
         *
         * @param m Boolean representing whether the robot uses encoder motors
         */
        public void setHasEncodedMotors(boolean m)
        {
                this.hasEncodedMotors = m;
        }

        /**
         * Gets whether the robot is using motors with encoders
         *
         * @return Boolean representing whether the robot uses encoder motors
         */
        public boolean getHasEncodedMotors()
        {
                return this.hasEncodedMotors;
        }

        /**
         * Return the value of the compass sensor on analog pins 4 and 5.
         *
         * An error is displayed if something goes wrong, but verbose is
         * required for more in-depth errors.
         *
         * @return Integer representing the heading of the compass in degrees.
         */
        public int readCompass()
        {
                if (!isConnected())
                {
                        error("Robot is not connected!", "RXTXRobot", "readCompass");
                        return -1;
                }
                try
                {
                        this.attemptTryAgain = true;
                        String[] split = sendRaw("c", 300).split("\\s+");
                        this.attemptTryAgain = false;
                        if (split.length <= 1)
                        {
                                error("No response was received from the Arduino.", "RXTXRobot", "readCompass");
                                return -1;
                        }
                        if (split.length - 1 != 1)
                        {
                                error("Incorrect length returned: " + split.length + ".", "RXTXRobot", "readCompass");
                                if (getVerbose())
                                        for (int x = 0; x < split.length; ++x)
                                                this.getErrStream().println("[" + x + "] = " + split[x]);
                                return -1;
                        }
                        return Integer.parseInt(split[1]);
                }
                catch (NumberFormatException e)
                {
                        error("Returned string could not be parsed into an Integer.", "RXTXRobot", "readCompass");
                }
                catch (Exception e)
                {
                        error("A generic error occurred.", "RXTXRobot", "readCompass");
                        if (getVerbose())
                        {
                                this.getErrStream().println("Stacktrace: ");
                                e.printStackTrace(this.getErrStream());
                        }
                }
                return -1;
        }
}
