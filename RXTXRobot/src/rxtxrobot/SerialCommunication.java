package rxtxrobot;

import gnu.io.CommPortIdentifier;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Chris King
 * @version 3.0.0
 */
public abstract class SerialCommunication
{
        /*
         * Private variables
         */
        final private static String API_VERSION = "3.0.0";
        private String port;
        private boolean verbose;
        private int baud_rate;

        public SerialCommunication()
        {
                baud_rate = 9600;
                port = "";
                verbose = false;
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
         * Gets an Array of the possible acceptable ports to connect a device.
         *
         * An array of the ports that might be the device you wish to connect
         * to.
         *
         * @return An array of Strings of the acceptable ports
         */
        public static String[] checkValidPorts()
        {
                List<String> list = new ArrayList<String>();
                Enumeration e = CommPortIdentifier.getPortIdentifiers();
                while (e.hasMoreElements())
                {
                        CommPortIdentifier portId = (CommPortIdentifier) e.nextElement();
                        if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
                                list.add(portId.getName());
                }
                return (String[]) list.toArray();
        }

        /**
         * Sets the port to connect to for serial communication.
         *
         * Set the port to the same port that the device is connected to on your
         * computer. <br /><br /> For example:<br /> On Windows, "COM3" (check
         * device manager)<br /> On Mac, "/dev/tty.usbmodem411" (run "ls /dev |
         * grep usb")<br /> On Linux, "/dev/ttyACM0"<br />
         *
         * @param p Port name that the device is connected to.
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
         * Sets verbose output.
         *
         * Setting verbose output allows for easier debugging and seeing what is
         * happening behind the scenes. Turn this on to debug your code.
         *
         * @param v Boolean to set verbosity.
         */
        public void setVerbose(boolean v)
        {
                this.verbose = v;
        }

        /**
         * Gets the verbose setting.
         *
         * @return The boolean for the verbose setting.
         */
        public boolean getVerbose()
        {
                return this.verbose;
        }

        /**
         * Sets the baud rate.
         *
         * Setting baud rate allows for increasing the communication speed. You
         * must ensure the firmware agrees with this setting. Do not touch this
         * if you don't know what it is.
         *
         * @param br Integer for the Baud Rate.
         */
        public void setBaudRate(int br)
        {
                this.baud_rate = br;
        }

        /**
         * Gets the baud rate.
         *
         * @return The integer of the baud rate.
         */
        public int getBaudRate()
        {
                return this.baud_rate;
        }

        /**
         * Prints out debugging statements.
         *
         * If verbose is set to true, then debugging statements will be printed
         * out to the user. If verbose is set to false, then these statements
         * will not print.
         *
         * @param str Message passed to debug
         */
        protected void debug(String str)
        {
                if (this.verbose)
                        System.out.println("--> " + str);
        }

        /**
         * Sets the output PrintStream; System.out by default.
         *
         * This method lets you set the output stream from System.out to
         * somewhere else. It shouldn't be used unless you know what you are
         * doing.
         *
         * @param p PrintStream to write output to.
         */
        public void setOutStream(PrintStream o)
        {
                System.setOut(o);
        }

        /**
         * Sets the error PrintStream; System.err by default.
         *
         * This method lets you set the error stream from System.err to
         * somewhere else. It shouldn't be used unless you know what you are
         * doing.
         *
         * @param p PrintStream to write error to.
         */
        public void setErrStream(PrintStream e)
        {
                System.setErr(e);
        }

        /**
         * Allows the robot to sleep for a time length (measured in
         * milliseconds).
         *
         * Uses a standard {@link Thread#sleep(long) Thread.sleep(long)}
         * function to pause execution of the program for the specified
         * milliseconds. Displays an error if the thread is interrupted during
         * this process, but does not throw an Exception. (1000 milliseconds = 1
         * second)
         *
         * @param amount The amount of time in milliseconds
         */
        public void sleep(int length)
        {
                try
                {
                        Thread.sleep(length);
                }
                catch (InterruptedException e)
                {
                        System.err.println("ERROR: Thread was interrupted (InterruptedException).  Error: " + e.toString());
                }
        }

        /**
         * Checks if the object is connected to the device.
         *
         * Returns true if the object is successfully connected to the device.
         * Returns false otherwise.
         *
         * @return true/false value that specifies if the Labview object is
         * connected to the Labview device.
         */
        public abstract boolean isConnected();

        /**
         * Closes the connection to the device.
         *
         * This method closes the serial connection to the device. It deletes
         * the mutual exclusion lock file, which is important, so this should be
         * done before the program is terminated.
         */
        public abstract void close();

        /**
         * Attempts to connect to the device.
         *
         * This method attempts to make a serial connection to the device if the
         * port is correct. If there is an error in connecting, then the
         * appropriate error message will be displayed. <br /><br /> This
         * function will terminate runtime if an error is discovered.
         */
        public abstract void connect();
}
