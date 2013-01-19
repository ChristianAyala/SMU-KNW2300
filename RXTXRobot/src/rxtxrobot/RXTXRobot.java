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
import com.phidgets.EncoderPhidget;
import com.phidgets.PhidgetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chris King
 * @version 3.0.0
 */
public class RXTXRobot extends SerialCommunication
{
        /*
         * Constants - These should not change unless you know what you are
         * doing
         */
        final private static boolean ONLY_ALLOW_TWO_MOTORS = true;
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
         * The maximum number of digital pins that can be read from the Arduino
         * (0 &lt; pins &lt; NUM_DIGITAL_PINS)
         */
        final public static int NUM_DIGITAL_PINS = 1;
        /**
         * The maximum number of analog pins that can be read from the Arduino
         * (0 &lt; pins &lt; NUM_ANALOG_PINS)
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
        private EncoderPhidget encMotor;
        // This is a flag to set if the communication should try again
        private boolean attemptTryAgain;

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
                overrideValidation = false;
                attemptTryAgain = false;
                hasEncodedMotors = false;
        }

        private String displayPossiblePorts()
        {
                String ret = "Possible serial ports:\n";
                String[] temp = SerialCommunication.checkValidPorts();
                int x = 0;
                for (; x < temp.length; ++x)
                        ret += "\t" + (x + 1) + ". " + temp[x] + "\n";
                if (x == 0)
                        ret += "\tNone - Make sure its plugged in!\n";
                return ret;
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
                        this.getErrStream().println("FATAL ERROR: No port was specified to connect to! (method: connect())\n");
                        this.getErrStream().println(displayPossiblePorts());
                        System.exit(1);
                }
                if (isConnected())
                {
                        this.getErrStream().println("ERROR: Robot is already connected! (method: connect())");
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
                                this.getErrStream().println("FATAL ERROR: Arduino port (" + getPort() + ") is currently owned by " + pIdent.getCurrentOwner() + "! (method: connect())\n");
                                this.getErrStream().println(displayPossiblePorts());
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
                                if (this.getHasEncodedMotors())
                                {
                                        this.getOutStream().println("Connecting encoded motors...\n");
                                        this.encMotor = new EncoderPhidget();
                                        this.encMotor.openAny();
                                        for (int x = 0; x < 50; ++x)
                                        {
                                                if (encMotor.isAttached())
                                                        break;
                                                sleep(500);
                                        }
                                        if (!encMotor.isAttached())
                                        {
                                                this.getErrStream().println("ERROR: Could not find attached encoded motors in time");
                                                System.exit(1);
                                        }
                                        else
                                        {
                                                this.getOutStream().println("Motors connected!\n");
                                        }
                                }
                        }
                }
                catch (NoSuchPortException e)
                {
                        this.getErrStream().println("FATAL ERROR: Invalid port (NoSuchPortException).  Check to make sure the correct port is set at the object's initialization. (method: connect())\n");
                        this.getErrStream().println(displayPossiblePorts());
                        if (getVerbose())
                        {
                                this.getErrStream().println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
                                e.printStackTrace(this.getErrStream());
                        }
                        System.exit(1);
                }
                catch (PortInUseException e)
                {
                        this.getErrStream().println("FATAL ERROR: Port is already being used by a different application (PortInUseException).  Did you stop a previously running instance of this program? (method: connect())\n");
                        this.getErrStream().println(displayPossiblePorts());
                        if (getVerbose())
                        {
                                this.getErrStream().println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
                                e.printStackTrace(this.getErrStream());
                        }
                        System.exit(1);
                }
                catch (UnsupportedCommOperationException e)
                {
                        this.getErrStream().println("FATAL ERROR: Comm Operation is unsupported (UnsupportedCommOperationException).  This shouldn't ever happen.  If you see this, ask a TA for assistance (method: connect())");
                        if (getVerbose())
                        {
                                this.getErrStream().println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
                                e.printStackTrace(this.getErrStream());
                        }
                        System.exit(1);
                }
                catch (IOException e)
                {
                        this.getErrStream().println("FATAL ERROR: Could not assign Input and Output streams (IOException).  You may be calling the \"close()\" method before this one.  Make sure you only call \"close()\" at the very end of your program. (method: connect())");
                        if (getVerbose())
                        {
                                this.getErrStream().println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
                                e.printStackTrace(this.getErrStream());
                        }
                        System.exit(1);
                }
                catch (Exception e)
                {
                        this.getErrStream().println("FATAL ERROR: Something went wrong. (method: connect())");
                        if (getVerbose())
                        {
                                this.getErrStream().println("Error Message: " + e.toString() + "\n\nError StackTrace:\n");
                                e.printStackTrace(this.getErrStream());
                        }
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
                        this.moveBothServos(90, 90);
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

        private String sendRaw(String str, int sleep)
        {
                debug("Sending command: " + str);
                if (!isConnected())
                {
                        this.getErrStream().println("ERROR: Cannot send command because the robot is not connected! (method: sendRaw())");
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
                                if (in.available() == 0 && attemptTryAgain && retries != 0)
                                {
                                        debug("No response from the Arduino....Trying " + retries + " more times");
                                }
                                if (retries == 0)
                                {
                                        this.getErrStream().println("There was no response from the Arduino, even after " + retries + " tries.");
                                }
                        }
                        while (in.available() == 0 && attemptTryAgain && retries != 0);
                        int bytesRead = in.read(buffer);
                        String ret = (new String(buffer)).trim();
                        debug("Received " + bytesRead + " bytes from the Arduino: " + ret);
                        return ret;
                }
                catch (IOException e)
                {
                        this.getErrStream().println("ERROR: Could not read or use Input or Output streams (IOException).  (method: sendRaw())");
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
                        this.getErrStream().println("ERROR: Robot is not connected!");
                        return;
                }
                try
                {
                        attemptTryAgain = true;
                        String[] split = sendRaw("r a").split("\\s+");
                        attemptTryAgain = false;
                        if (split.length <= 1)
                        {
                                this.getErrStream().println("ERROR: No response was received from the Arduino.  Try again. (method: refreshAnalogPins())");
                                return;
                        }
                        if (split.length - 1 != RXTXRobot.NUM_ANALOG_PINS)
                        {
                                this.getErrStream().println("ERROR: Incorrect length returned: " + split.length + ".  (method: refreshAnalogPins())");
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
                        this.getErrStream().println("ERROR: Returned string could not be parsed into Integers.  (method: refreshAnalogPins())");
                }
                catch (Exception e)
                {
                        this.getErrStream().println("ERROR: An error occurred with getAnalogPins.");
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
                        this.getErrStream().println("ERROR: Robot is not connected!");
                        return;
                }
                try
                {
                        attemptTryAgain = true;
                        String[] split = sendRaw("r d").split("\\s+");
                        attemptTryAgain = false;
                        if (split.length <= 1)
                        {
                                this.getErrStream().println("ERROR: No response was received from the Arduino.  Try again. (method: refreshDigitalPins())");
                                return;
                        }
                        if (split.length - 1 != RXTXRobot.NUM_DIGITAL_PINS)
                        {
                                this.getErrStream().println("ERROR: Incorrect length returned: " + split.length + ".  (method: refreshDigitalPins())");
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
                        this.getErrStream().println("ERROR: Returned string could not be parsed into Integers.  (method: refreshDigitalPins())");
                }
                catch (Exception e)
                {
                        this.getErrStream().println("ERROR: An error occurred with getDigitalPins.");
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
                        this.getErrStream().println("ERROR: Analog pin " + x + " doesn't exist.  (method: getAnalogPin())");
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
                final int[][] mapping =
                {
                        {
                                13, 0
                        }
                };
                if (digitalPinCache == null)
                        this.refreshDigitalPins();
                int get_pin = -1;
                for (int y = 0; y < mapping.length; ++y)
                {
                        if (mapping[y][0] == x)
                                return new DigitalPin(x, digitalPinCache[mapping[y][1]]);
                }
                this.getErrStream().println("ERROR: Digital pin " + x + " doesn't exist.  (method: getDigitalPin())");
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
                        this.getErrStream().println("ERROR: Robot is not connected!");
                        return -1;
                }
                try
                {
                        String[] split = sendRaw("r t").split("\\s+");
                        if (split.length <= 1)
                        {
                                this.getErrStream().println("No response was received from the Arduino.  Try again.  (method: getTemperature())");
                                return -1;
                        }
                        if (split.length - 1 != 1)
                        {
                                this.getErrStream().println("Incorrect length returned: " + split.length + ".  (method: getTemperature())");
                                if (getVerbose())
                                        for (int x = 0; x < split.length; ++x)
                                                this.getErrStream().println("[" + x + "] = " + split[x]);
                                return -1;
                        }
                        return Integer.parseInt(split[1]);
                }
                catch (NumberFormatException e)
                {
                        this.getErrStream().println("ERROR: Returned string could not be parsed into an Integer.  (method: getTemperature())");
                }
                catch (Exception e)
                {
                        this.getErrStream().println("ERROR: An error occurred with getTemperature()");
                        if (getVerbose())
                        {
                                this.getErrStream().println("Stacktrace: ");
                                e.printStackTrace(this.getErrStream());
                        }
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
         * {@link #SERVO1 RXTXRobot.SERVO1} or {@link #SERVO2 RXTXRobot.SERVO2}.
         * @param position The position (in degrees) where you want the servo to
         * turn to: 0 &lt; position &lt; 180.
         */
        public void moveServo(int servo, int position)
        {
                if (!isConnected())
                {
                        this.getErrStream().println("ERROR: Robot is not connected!");
                        return;
                }
                if (!getOverrideValidation() && servo != RXTXRobot.SERVO1 && servo != RXTXRobot.SERVO2)
                {
                        this.getErrStream().println("ERROR: Invalid servo argument (RXTXRobot.SERVO1 or RXTXRobot.SERVO2).  (method: moveServo())");
                        return;
                }
                debug("Moving servo " + servo + " to position " + position);
                if (!getOverrideValidation() && (position < 0 || position > 180))
                        this.getErrStream().println("ERROR: Position must be >=0 and <=180.  You supplied " + position + ", which is invalid.  (method: moveServo())");
                else
                        sendRaw("v " + servo + " " + position);
        }

        /**
         * Moves both servos simultaneously to the desired positions.
         *
         * Accepts two angular positions between 0 and 180 inclusive and moves
         * the servo motors to the corresponding angular position.
         * {@link #SERVO1 SERVO1} moves {@code pos1} degrees and
         * {@link #SERVO2 SERVO2} moves {@code pos2} degrees. <br /><br /> The
         * servos start at 90 degrees, so a number &lt; 90 will turn it one way,
         * and a number &gt; 90 will turn it the other way. <br /><br /> An
         * error message will be displayed on error.
         *
         * @param pos1 The angular position of RXTXRobot.SERVO1
         * @param pos2 The angular position of RXTXRobot.SERVO2
         */
        public void moveBothServos(int pos1, int pos2)
        {
                if (!isConnected())
                {
                        this.getErrStream().println("ERROR: Robot is not connected!");
                        return;
                }
                debug("Moving both servos to positions " + pos1 + " and " + pos2);
                if (!getOverrideValidation() && (pos1 < 0 || pos1 > 180 || pos2 < 0 || pos2 > 180))
                        this.getErrStream().println("ERROR: Positions must be >=0 and <=180.  You supplied " + pos1 + " and " + pos2 + ".  One or more are invalid.  (method: moveBothServos())");
                else
                        sendRaw("V " + pos1 + " " + pos2);
        }

        /**
         * Runs a DC motor at a specific speed for a specific time (Potential
         * blocking method).
         *
         * Accepts a DC motor, either {@link #MOTOR1 RXTXRobot.MOTOR1},
         * {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}, the speed that the motor should
         * run at (-255 - 255), and the time with which the motor should run (in
         * milliseconds). <br /><br /> If speed is negative, the motor will run
         * in reverse. <br /><br /> If time is 0, the motor will run infinitely
         * until another call to that motor is made, even if the Java program
         * terminates. <br /><br /> An error message will display on error. <br
         * /><br /> Note: This method is a blocking method unless time = 0
         *
         * @param motor The DC motor you want to run:
         * {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}
         * @param speed The speed that the motor should run at (-255 - 255)
         * @param time The number of milliseconds the motor should run (0 for
         * infinite) (may not be above 30,000 (30 seconds))
         */
        public void runMotor(int motor, int speed, int time)
        {
                if (!isConnected())
                {
                        this.getErrStream().println("ERROR: Robot is not connected!");
                        return;
                }
                if (!getOverrideValidation() && (speed < -255 || speed > 255))
                {
                        this.getErrStream().println("ERROR: You must give the motors a speed between -255 and 255 (inclusive).  (method: runMotor())");
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
                        this.getErrStream().println("ERROR: runMotor was not given a time that is 0 <= time <= 30000.  (method: runMotor())");
                        return;
                }
                if (!getOverrideValidation() && (motor < RXTXRobot.MOTOR1 || motor > RXTXRobot.MOTOR4))
                {
                        this.getErrStream().println("ERROR: runMotor was not given a correct motor argument.  (method: runMotor())");
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
         * should run (-255 - 255), accepts another DC motor, the speed in which
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
                        this.getErrStream().println("ERROR: Robot is not connected!");
                        return;
                }
                if (!getOverrideValidation() && (speed1 < -255 || speed1 > 255 || speed2 < -255 || speed2 > 255))
                {
                        this.getErrStream().println("ERROR: You must give the motors a speed between -255 and 255 (inclusive).  (method: runMotor())");
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
                        this.getErrStream().println("ERROR: runMotor was not given a time that is 0 <= time <= 30000.  (method: runMotor())");
                        return;
                }
                if (!getOverrideValidation() && ((motor1 < RXTXRobot.MOTOR1 || motor1 > RXTXRobot.MOTOR4) || (motor2 < RXTXRobot.MOTOR1 || motor2 > RXTXRobot.MOTOR4)))
                {
                        this.getErrStream().println("ERROR: runMotor was not given a correct motor argument.  (method: runMotor())");
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
         * should run (-255 - 255), accepts another DC motor, the speed in which
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
        public void runMotor(int motor1, int speed1, int motor2, int speed2, int motor3, int speed3, int motor4, int speed4, int time)
        {
                if (!isConnected())
                {
                        this.getErrStream().println("ERROR: Robot is not connected!");
                        return;
                }
                if (!getOverrideValidation() && (speed1 < -255 || speed1 > 255 || speed2 < -255 || speed2 > 255 || speed3 < -255 || speed3 > 255 || speed4 < -255 || speed4 > 255))
                {
                        this.getErrStream().println("ERROR: You must give the motors a speed between -255 and 255 (inclusive).  (method: runMotor())");
                        return;
                }
                if (!getOverrideValidation() && RXTXRobot.ONLY_ALLOW_TWO_MOTORS)
                {
                        this.getErrStream().println("ERROR: You may only run two DC motors at a time, so you cannot use this method!  (method: runMotor())");
                        return;
                }
                if (!getOverrideValidation() && time < 0)
                {
                        this.getErrStream().println("ERROR: runMotor was not given a time that is >=0.  (method: runMotor())");
                        return;
                }
                if (!getOverrideValidation() && ((motor1 < 0 || motor1 > 3) || (motor2 < 0 || motor2 > 3) || (motor3 < 0 || motor3 > 3) || (motor4 < 0 || motor4 > 3)))
                {
                        this.getErrStream().println("ERROR: runMotor was not given a correct motor argument.  (method: runMotor())");
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
         * Accepts a DC motor, either {@link #MOTOR1 RXTXRobot.MOTOR1},
         * {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}, the speed that the motor should
         * run at (-255 - 255), and the tick to move to. <br /><br /> If speed
         * is negative, the motor will run in reverse. <br /><br /> An error
         * message will display on error. <br /><br /> Note: This method is a
         * blocking method
         *
         * @param motor The DC motor you want to run:
         * {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}
         * @param speed The speed that the motor should run at (-255 - 255)
         * @param tick The tick that the motor should move
         */
        public void runEncodedMotor(int motor, int speed, int ticks)
        {
                if (!isConnected())
                {
                        this.getErrStream().println("ERROR: Robot is not connected!  (method: runEncodedMotor())");
                        return;
                }
                try
                {
                        if (!getOverrideValidation() && (!this.getHasEncodedMotors() || !this.encMotor.isAttached()))
                        {
                                this.getErrStream().println("ERROR: Encoded motors are not on or attached  (method: runEncodedMotor())");
                                return;
                        }
                }
                catch (Exception e)
                {
                        this.getErrStream().println("EXCEPTION: Encoded motors are not on or attached  (method: runEncodedMotor())");
                        return;
                }
                if (!getOverrideValidation() && (speed < -255 || speed > 255))
                {
                        this.getErrStream().println("ERROR: You must give the motors a speed between -255 and 255 (inclusive).  (method: runEncodedMotor())");
                        return;
                }
                if (!getOverrideValidation() && (motor < RXTXRobot.MOTOR1 || motor > RXTXRobot.MOTOR4))
                {
                        this.getErrStream().println("ERROR: runEncodedMotor was not given a correct motor argument.  (method: runEncodedMotor())");
                        return;
                }
                if (!getOverrideValidation() && (ticks <= 0))
                {
                        this.getErrStream().println("ERROR: runEncodedMotor was not given a positive tick argument.  (method: runEncodedMotor())");
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
                int curTicks = 0;
                try
                {
                        curTicks = encMotor.getPosition(motor);
                }
                catch (Exception e)
                {
                }
                int goalTicks;
                if (speed > 0)
                        goalTicks = curTicks + ticks;
                else
                        goalTicks = curTicks - ticks;
                if (!"".equals(sendRaw("d " + motor + " " + speed + " 0")))
                {
                        if (speed != 0)
                        {
                                int test;
                                while (true)
                                {
                                        try
                                        {
                                                test = encMotor.getPosition(motor);
                                                if (speed > 0 && test > goalTicks)
                                                {
                                                        break;
                                                }
                                                else if (speed < 0 && test < goalTicks)
                                                {
                                                        break;
                                                }
                                        }
                                        catch (Exception ex)
                                        {
                                        }
                                }
                        }
                        this.attemptTryAgain = true;
                        sendRaw("d " + motor + " 0 0");
                        this.attemptTryAgain = false;
                }
                motorsRunning[motor] = false;
        }

        /**
         * Runs a DC encoded motor at a specific speed for a specific distance
         * (Blocking Method).
         *
         * Accepts a DC motor, either {@link #MOTOR1 RXTXRobot.MOTOR1},
         * {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}, the speed in which that motor
         * should run (-255 - 255), accepts another DC motor, the speed in which
         * that motor should run, and the time with which both motors should run
         * (in milliseconds). <br /><br /> If speed is negative for either
         * motor, that motor will run in reverse. <br /><br /> An error message
         * will display on error. <br /><br /> Note: This method is a blocking
         * method unless time = 0
         *
         * @param motor1 The first DC motor:
         * {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}
         * @param speed1 The speed that the first DC motor should run at
         * @param tick1 The tick that the first DC motor should move to
         * @param motor2 The second DC motor:
         * {@link #MOTOR1 RXTXRobot.MOTOR1}, {@link #MOTOR2 RXTXRobot.MOTOR2}, {@link #MOTOR3 RXTXRobot.MOTOR3},
         * or {@link #MOTOR4 RXTXRobot.MOTOR4}
         * @param speed2 The speed that the second DC motor should run at
         * @param ticks2 The tick that the second DC motor should move
         */
        public void runEncodedMotor(int motor1, int speed1, int tick1, int motor2, int speed2, int tick2)
        {
                if (!isConnected())
                {
                        this.getErrStream().println("ERROR: Robot is not connected!  (method: runEncodedMotor())");
                        return;
                }
                try
                {
                        if (!getOverrideValidation() && (!this.getHasEncodedMotors() || !this.encMotor.isAttached()))
                        {
                                this.getErrStream().println("ERROR: Encoded motors are not on or attached  (method: runEncodedMotor())");
                                return;
                        }
                }
                catch (Exception e)
                {
                        this.getErrStream().println("EXCEPTION: Encoded motors are not on or attached  (method: runEncodedMotor())");
                        return;
                }
                if (!getOverrideValidation() && (speed1 < -255 || speed1 > 255 || speed2 < -255 || speed2 > 255))
                {
                        this.getErrStream().println("ERROR: You must give the motors a speed between -255 and 255 (inclusive).  (method: runEncodedMotor())");
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
                        this.getErrStream().println("ERROR: runEncodedMotor was not given a tick that is positive  (method: runEncodedMotor())");
                        return;
                }
                if (!getOverrideValidation() && ((motor1 < RXTXRobot.MOTOR1 || motor1 > RXTXRobot.MOTOR4) || (motor2 < RXTXRobot.MOTOR1 || motor2 > RXTXRobot.MOTOR4)))
                {
                        this.getErrStream().println("ERROR: runEncodedMotor was not given a correct motor argument.  (method: runEncodedMotor())");
                }
                debug("Running two motors, motor " + motor1 + " at speed " + speed1 + " for " + tick1 + " ticks and motor " + motor2 + " at speed " + speed2 + " for " + tick2 + " ticks");
                int curTicks1 = 0;
                int curTicks2 = 0;
                try
                {
                        curTicks1 = encMotor.getPosition(motor1);
                        curTicks2 = encMotor.getPosition(motor2);
                }
                catch (Exception e)
                {
                }
                int goalTicks1;
                int goalTicks2;
                if (speed1 > 0)
                        goalTicks1 = curTicks1 + tick1;
                else
                        goalTicks1 = curTicks1 - tick1;
                if (speed2 > 0)
                        goalTicks2 = curTicks2 + tick2;
                else
                        goalTicks2 = curTicks2 - tick2;
                if (!"".equals(sendRaw("D " + motor1 + " " + speed1 + " " + motor2 + " " + speed2 + " 0")))
                {
                        if (speed1 != 0)
                        {
                                int test1;
                                int test2;
                                boolean firstDone = false;
                                boolean secondDone = false;
                                while (true)
                                {
                                        try
                                        {
                                                test1 = encMotor.getPosition(motor1);
                                                test2 = encMotor.getPosition(motor2);
                                                if (speed1 > 0 && test1 > goalTicks1)
                                                {
                                                        firstDone = true;
                                                        if (tick1 == tick2)
                                                        {
                                                                secondDone = true;
                                                                sendRaw("D " + motor1 + " 0 " + motor2 + " 0 0");
                                                        }
                                                        else
                                                                sendRaw("d " + motor1 + " 0 0 ");
                                                }
                                                else if (speed1 < 0 && test1 < goalTicks1)
                                                {
                                                        firstDone = true;
                                                        if (tick1 == tick2)
                                                        {
                                                                secondDone = true;
                                                                sendRaw("D " + motor1 + " 0 " + motor2 + " 0 0");
                                                        }
                                                        else
                                                                sendRaw("d " + motor1 + " 0 0 ");
                                                }
                                                if (speed2 > 0 && test2 > goalTicks2)
                                                {
                                                        secondDone = true;
                                                        if (tick1 == tick2)
                                                        {
                                                                firstDone = true;
                                                                sendRaw("D " + motor1 + " 0 " + motor2 + " 0 0");
                                                        }
                                                        else
                                                                sendRaw("d " + motor2 + " 0 0 ");
                                                }
                                                else if (speed2 < 0 && test2 < goalTicks2)
                                                {
                                                        firstDone = true;
                                                        if (tick1 == tick2)
                                                        {
                                                                secondDone = true;
                                                                sendRaw("D " + motor1 + " 0 " + motor2 + " 0 0");
                                                        }
                                                        else
                                                                sendRaw("d " + motor2 + " 0 0 ");
                                                }
                                                if (firstDone && secondDone)
                                                        break;
                                        }
                                        catch (Exception ex)
                                        {
                                        }
                                }
                        }
                }
                motorsRunning[motor1] = false;
                motorsRunning[motor2] = false;
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
                        this.getErrStream().println("ERROR: You may not run more than two motors at any given time!");
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
                        this.getErrStream().println("ERROR: Robot is not connected!");
                        return;
                }
                if (!getOverrideValidation() && (motor < RXTXRobot.MOTOR1 || motor > RXTXRobot.MOTOR4))
                {
                        this.getErrStream().println("ERROR: You must supply a valid motor port: RXTXRobot.MOTOR1, MOTOR2, MOTOR3, or MOTOR4.  (method: runMixer())");
                        return;
                }
                if (!getOverrideValidation() && time < 0)
                {
                        this.getErrStream().println("ERROR: You must supply a positive time.  (method: runMixer())");
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
                        this.getErrStream().println("ERROR: Robot is not connected!");
                        return;
                }
                if (!getOverrideValidation() && (motor < RXTXRobot.MOTOR1 || motor > RXTXRobot.MOTOR4))
                {
                        this.getErrStream().println("ERROR: You must supply a valid motor port: RXTXRobot.MOTOR1, MOTOR2, MOTOR3, or MOTOR4.  (method: stopMixer())");
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
                        this.getErrStream().println("WARNING: The speed supplied (" + speed + ") is > 255.  Resetting it to 255.  (method: setMixerSpeed())");
                        speed = 255;
                }
                if (!getOverrideValidation() && speed < 0)
                {
                        this.getErrStream().println("WARNING: The speed supplied (" + speed + ") is < 0.  Resetting it to 0.  (method: setMixerSpeed())");
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
         * @param r Boolean representing whether the robot uses encoder motors
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
}
