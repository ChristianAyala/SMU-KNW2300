package rxtxrobot;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Chris King
 * @version 3.1.5
 */
public class Labview extends SerialCommunication
{
        public static int MODE_3D = 1;
        public static int MODE_2D = 2;
        private SerialPort sPort;
        private CommPort cPort;
        private ServerSocket sSocket;
        private Socket socket;
        private PrintWriter write;
        private BufferedReader read;

        public Labview()
        {
                super();
        }

        /**
         * Checks if the Labview object is connected to the Labview device.
         *
         * Returns true if the Labview object is successfully connected to the
         * Labview device. Returns false otherwise.
         *
         * @return true/false value that specifies if the Labview object is
         * connected to the Labview device.
         */
        @Override
        public boolean isConnected()
        {
                return sPort != null && cPort != null && socket.isConnected();
        }

        /**
         * Closes the connection to the Labview device.
         *
         * This method closes the serial connection to the Labview device. It
         * deletes the mutual exclusion lock file, which is important, so this
         * should be done before the program is terminated.
         */
        @Override
        public void close()
        {
                try
                {
                        sSocket.close();
                }
                catch (Exception e)
                {
                }
                try
                {
                        socket.close();
                }
                catch (Exception e)
                {
                }
                if (cPort != null)
                        cPort.close();
                if (sPort != null)
                        sPort.close();
                cPort = null;
                sPort = null;
        }

        /**
         * Attempts to connect to Labview.
         *
         * This method attempts to make a serial connection to Labview if the
         * port is correct. If there is an error in connecting, then the
         * appropriate error message will be displayed. <br><br> This
         * function will terminate runtime if an error is discovered.
         */
        @Override
        public void connect()
        {
                if ("".equals(getPort()))
                {
                        error("No port was specified for Labview to connect to!", "Labview", "connect", true);
                        System.exit(1);
                }
                if (isConnected())
                {
                        error("Labview is already connected!", "Labview", "connect");
                        return;
                }
                try
                {
                        sSocket = new ServerSocket(1337);
                        this.getOutStream().println("Waiting for labview to connect... Make sure Labview is connecting to the correct IP address on port 1337");
                        socket = sSocket.accept();
                        write = new PrintWriter(socket.getOutputStream(), true);
                        read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        this.getOutStream().println("Labview connected!  Starting to run program...");
                }
                catch (Exception e)
                {
                        error("An error occurred waiting for labview: " + e.getMessage(), "Labview", "connect");
                        if (this.getVerbose())
                                e.printStackTrace(this.getErrStream());
                        System.exit(1);
                }
                try
                {
                        CommPortIdentifier pIdent = CommPortIdentifier.getPortIdentifier(getPort());
                        if (pIdent.isCurrentlyOwned())
                        {
                                error("Labview port (" + getPort() + ") is currently owned by " + pIdent.getCurrentOwner(), "Labview", "connect", true);
                                System.exit(1);
                        }
                        cPort = pIdent.open("LabView", 2000);
                        if (cPort instanceof SerialPort)
                        {
                                sPort = (SerialPort) cPort;
                                sPort.setSerialPortParams(getBaudRate(), SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                        }
                        this.getOutStream().println("Labview is now ready!");
                }
                catch (Exception e)
                {
                        error("There was an error connecting to Labview", "Labview", "connect");
                }
        }

        public Coord[] read(int mode)
        {
                if (mode < Labview.MODE_3D || mode > Labview.MODE_2D)
                {
                        error("Invalid MODE was given.", "Labview", "read");
                        return null;
                }
                if (!socket.isConnected() || !isConnected())
                {
                        error("The connection to labview was lost.", "Labview", "read");
                        return null;
                }
                String command = "s";
                debug("Sending command: " + command);
                String lastResponse = "";
                try
                {
                        write.write(command);
                        write.flush();
                        sleep(200);
                        while (read.ready())
                                lastResponse += (char) read.read();
                        lastResponse = lastResponse.substring(lastResponse.indexOf("[") + 1, lastResponse.indexOf("]"));
                        String[] parts = lastResponse.split(",");
                        if (mode == Labview.MODE_3D)
                        {
                                debug("Creating Coord (3D mode): (" + parts[0] + ", " + parts[1] + ", " + parts[2] + ")");
                                Coord[] ans =
                                {
                                        new Coord(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]))
                                };
                                return ans;
                        }
                        else if (mode == Labview.MODE_2D)
                        {
                                debug("Creating two Coords (2D mode): (" + parts[0] + ", " + parts[1] + ") and (" + parts[2] + ", " + parts[3] + ")");
                                Coord[] ans =
                                {
                                        new Coord(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), -1.0), new Coord(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), -1.0)
                                };
                                return ans;
                        }
                }
                catch (NumberFormatException e)
                {
                        debug("Labview returned: " + lastResponse + " and determined it not to be a number.  (method: read())");
                }
                catch (Exception e)
                {
                        error("A generic error occurred.  Message: " + e.toString(), "Labview", "connect");
                }
                return null;
        }
}
