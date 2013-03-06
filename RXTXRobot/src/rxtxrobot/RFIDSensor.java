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
 * @version 3.1.1
 */
public class RFIDSensor extends SerialCommunication
{
        /*
         * Private variables
         */
        private InputStream in;
        private OutputStream out;
        private SerialPort sPort;
        private CommPort cPort;
        private String tag;
        private boolean running;
        private boolean hasTag;
        private Thread scanningThread;

        /**
         * Initialize the RFID Sensor.
         *
         */
        public RFIDSensor()
        {
                super();
                tag = "";
                running = false;
        }

        /**
         * Checks if the RFID Sensor object is connected to the actual sensor.
         *
         * Returns true if the RFID Sensor object is successfully connected to
         * the actual sensor. Returns false otherwise.
         *
         * @return true/false value that specifies if the RFID Sensor object is
         * connected to the sensor.
         */
        @Override
        public boolean isConnected()
        {
                return sPort != null && cPort != null && in != null && out != null;
        }

        /**
         * Closes the connection to the RFID Sensor.
         *
         * This method closes the serial connection to the RFID Sensor. It
         * deletes the mutual exclusion lock file, which is important, so this
         * should be done before the program is terminated.
         */
        @Override
        public void close()
        {
                running = false;
                this.getOutStream().println("Waiting for RFID Sensor to close...");
                for (int x = 0; x < 20 && scanningThread.isAlive(); ++x)
                {
                        sleep(500);
                }
                if (sPort != null)
                        sPort.close();
                if (cPort != null)
                        cPort.close();
                in = null;
                out = null;
                this.getOutStream().println("RFID Sensor connection closed!");
        }

        /**
         * Attempts to connect to the RFID Sensor.
         *
         * This method attempts to make a serial connection to the RFID Sensor.
         * If there is an error in connecting, then the appropriate error
         * message will be displayed. <br /><br /> This function will terminate
         * runtime if an error is discovered.
         */
        @Override
        public void connect()
        {
                this.getOutStream().println("Connecting to RFID Sensor, please wait...\n");
                if ("".equals(getPort()))
                {
                        error("No port was specified to connect to!\n" + SerialCommunication.displayPossiblePorts(), "RFIDSensor", "connect", true);
                        System.exit(1);
                }
                if (isConnected())
                {
                        error("RFID Sensor is already connected!", "RFIDSensor", "connect");
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
                                error("RFID Sensor port (" + getPort() + ") is currently owned by " + pIdent.getCurrentOwner() + "!\n" + SerialCommunication.displayPossiblePorts(), "RXTXRobot", "connect", true);
                                System.exit(1);
                        }
                        cPort = pIdent.open("RFIDSensor", 2000);
                        if (cPort instanceof SerialPort)
                        {
                                sPort = (SerialPort) cPort;
                                sPort.setSerialPortParams(getBaudRate(), SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                                sleep(500);
                                in = sPort.getInputStream();
                                out = sPort.getOutputStream();
                                sleep(1000);
                                this.getOutStream().println("RFID Sensor Connected!\n");
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

                running = true;
                scanningThread = new Thread(new ScanningThread());
                scanningThread.start();
        }

        /**
         * Determines if a new tag has been scanned.
         *
         * This method will return true if a new tag has been scanned since the
         * last time getTag was called.
         *
         * @return Boolean identifying if a new tag was scanned.
         */
        public boolean hasTag()
        {
                if (!this.isConnected())
                        this.error("RFIDSensor is not connected!", "RFIDSensor", "hasTag");
                return hasTag;
        }

        /**
         * Gets the scanned tag.
         *
         * This method will return the last tag that was scanned.
         *
         * @return String of the tag that was read.
         */
        public String getTag()
        {
                if (!this.isConnected())
                        this.error("RFIDSensor is not connected!", "RFIDSensor", "getTag");
                hasTag = false;
                return tag;
        }

        private class ScanningThread implements Runnable
        {
                @Override
                public void run()
                {
                        byte[] buffer = new byte[1024];
                        while (running)
                        {
                                synchronized (tag)
                                {
                                        try
                                        {
                                                if (in.available() > 5) // Threshold of 5
                                                {
                                                        in.read(buffer);
                                                        tag = (new String(buffer)).trim();
                                                        hasTag = true;
                                                }
                                                sleep(200);
                                        }
                                        catch (Exception e)
                                        {
                                                error("IOException happened inside Thread", "RFIDSensor", "run");
                                        }
                                }
                        }
                }
        }
}
