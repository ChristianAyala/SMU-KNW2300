package rxtxrobot;
import gnu.io.*;
import java.io.*;

public class RXTXRobot
{
    private String port;
    private boolean verbose;
    private OutputStream out;
    private InputStream in;
    private byte[] buffer;
    public RXTXRobot(String p)
    {
        port = p;
        verbose = false;
        buffer = new byte[1024];
        init();
    }
    public RXTXRobot(String p, boolean v)
    {
        port = p;
        verbose = v;
        buffer = new byte[1024];
        init();
    }
    public RXTXRobot(String p, boolean v, int bufferSize)
    {
        port = p;
        verbose = v;
        buffer = new byte[bufferSize];
        init();
    }
    private void init()
    {
        try
        {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(port);
            if (portIdentifier.isCurrentlyOwned())
            {
                System.err.println("Error: Port is currently in use");
            }
            else
            {
                    CommPort commPort = portIdentifier.open("RXRobot",2000);
                    if ( commPort instanceof SerialPort )
                    {
                        SerialPort serialPort = (SerialPort) commPort;
                        serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                        debug("Sleeping and resetting...");
                        Thread.sleep(3000);
                        debug("Waking up...");
                        in = serialPort.getInputStream();
                        out = serialPort.getOutputStream();
                    }
            }
        }
        catch(NoSuchPortException e)
        {
            System.err.println("Invalid port (NoSuchPortException). Error: " + e.getMessage());
        }
        catch(PortInUseException e)
        {
            System.err.println("Port is already being used by a different application (PortInUseException). Error: " + e.getMessage());
        }
        catch(UnsupportedCommOperationException e)
        {
            System.err.println("Comm Operation is unsupported (UnsupportedCommOperationException). Error: " + e.getMessage());
        }
        catch(InterruptedException e)
        {
            System.err.println("Thread was interrupted (InterruptedException). Error: " + e.getMessage());
        }
        catch(IOException e)
        {
            System.err.println("Could not assign Input and Output streams (IOException). Error: " + e.getMessage());
        }
    }
    private void debug(String s)
    {
        if (verbose)
            System.out.println("----> " + s);
    }
    public void sendRaw(String s)
    {
        debug("Sending command: " + s);
        try
        {
            if (out != null)
                out.write((s+"\r\n").getBytes());
        }
        catch(IOException e)
        {
            System.err.println("Cannot write command (IOException)! Error: " + e.getMessage());
        }
    }
    public void sleep(int length)
    {
        try
        {
            Thread.sleep(length);
        }
        catch(InterruptedException e)
        {
            System.err.println("Thread was interrupted (InterruptedException). Error: " + e.getMessage());
        }
    }
    public String readAnalogPins()
    {
        try
        {
            debug("Reading Analog Pins...");
            sendRaw("r a");
            in.read(buffer, 0, Math.min(in.available(), buffer.length));
            String t = new String(buffer);
            debug("Received response: " + t);
            return t;
        }
        catch(IOException e)
        {
            System.err.println("Cannot read command (IOException)! Error: " + e.getMessage());
        }
        return "";
    }
    public String readDigitalPins()
    {
        try
        {
            debug("Reading Analog Pins...");
            sendRaw("r d");
            in.read(buffer, 0, Math.min(in.available(), buffer.length));
            String t = new String(buffer);
            debug("Received response: " + t);
            return t;
        }
        catch(IOException e)
        {
            System.err.println("Cannot read command (IOException)! Error: " + e.getMessage());
        }
        return "";
    }
}