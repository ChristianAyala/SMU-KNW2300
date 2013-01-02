package rxtxrobot;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Chris King
 * @version 3.0.0
 */
public class Labview
{
	private String port;
	private boolean verbose;
	private int BAUD_RATE = 9600;

	private SerialPort sPort;
	private CommPort cPort;
	private ServerSocket sSocket;
	private Socket socket;
	private PrintWriter write;
	private BufferedReader read;
	private OutputStream out;
	private InputStream in;

	public Labview()
	{
		port = "";
		verbose = false;
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
	public void debug(String str)
	{
		if (verbose)
			System.err.println("--> " + str);
	}
        public boolean isConnected()
        {
            return sPort != null && cPort != null;
        }
	public final void setOutStream(PrintStream o)
	{
		System.setOut(o);
	}
	public final void setErrStream(PrintStream e)
	{
		System.setErr(e);
	}
        public void close()
        {
            try
            {
                sSocket.close();
            }
            catch(Exception e) {}
            try
            {
                socket.close();
            }
            catch(Exception e) {}
            if (cPort != null)
                cPort.close();
            if (sPort != null)
                sPort.close();
        }
	public void connect()
	{
		if ("".equals(port))
		{
			System.err.println("FATAL ERROR: No port was specified for Labview to connect to!  (method: connect())");
			System.exit(1);
		}
		if (isConnected())
		{
			System.err.println("ERROR: Labview is already connected!  (method: connect())");
			return;
		}
		try
		{
			sSocket = new ServerSocket(1337);
			System.out.println("Waiting for labview to connect... Make sure Labview is connecting to the correct IP address on port 1337");
			socket = sSocket.accept();
			write = new PrintWriter(socket.getOutputStream(),true);
			read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println("Labview connected!  Starting to run program...");
		}
		catch(Exception e)
		{
			System.err.println("An error occurred waiting for labview:" + e + "\n\nStacktrace: ");
			e.printStackTrace(System.err);
			System.exit(1);
		}
		try
		{
			CommPortIdentifier pIdent = CommPortIdentifier.getPortIdentifier(port);
			if (pIdent.isCurrentlyOwned())
			{
				System.err.println("FATAL ERROR: Labview port ("+port+") is currently owned by "+pIdent.getCurrentOwner());
				System.exit(1);
			}
			cPort = pIdent.open("LabView", 2000);
			if (cPort instanceof SerialPort)
			{
				sPort = (SerialPort)cPort;
				sPort.setSerialPortParams(BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				Thread.sleep(300);
				in = sPort.getInputStream();
				out = sPort.getOutputStream();
			}
			System.out.println("Labview is now ready!");
		}
		catch(Exception e)
		{
			System.err.println("ERROR: There was an error connecting LabView");
		}
	}
}











