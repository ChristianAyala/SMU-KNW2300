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
    private String lastResponse;
    private SerialPort serialPort;
    private CommPort commPort;
    private CommPortIdentifier portIdentifier;
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
            portIdentifier = CommPortIdentifier.getPortIdentifier(port);
            if (portIdentifier.isCurrentlyOwned())
            {
                System.err.println("Error: Port is currently in use");
            }
            else
            {
                    commPort = portIdentifier.open("RXRobot",2000);
                    if ( commPort instanceof SerialPort )
                    {
                        serialPort = (SerialPort) commPort;
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
    public void close()
    {
        if (serialPort != null)
            serialPort.close();
        if (commPort != null)
            commPort.close();
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
            if (out != null || in != null)
            {
                out.write((s+"\r\n").getBytes());
                /*if (verbose)
                {
                    sleep(2000);
                    in.read(buffer, 0, Math.min(in.available(), buffer.length));
                    lastResponse = new String(buffer);
                }*/
            }
        }
        catch(IOException e)
        {
            System.err.println("Cannot write command (IOException)! Error: " + e.getMessage());
        }
    }
    public String getLastResponse()
    {
        return lastResponse;
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
    public String getAnalogPins()
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
    public String getDigitalPins()
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
    public void setAnalogPin(int pin, int value)
    {
        debug("Setting Analog Pin " + pin + " to " + value);
        sendRaw("w a " + pin + " " + value);
    }
    public void setDigitalPin(int pin, int value)
    {
        debug("Setting Digital Pin " + pin + " to " + value);
        sendRaw("w d " + pin + " " + value);
    }
    public void moveServo(int servo, int position)
    {
        debug("Moving servo " + servo + " to position " + position);
        if (position <= 0 || position >= 180)
            System.err.println("ERROR: moveServo position must be greater than 0 and less than 180.  You supplied " + position + ", which is invalid.");
        else
            sendRaw("v " + servo + " " + position);
    }
    public void moveBothServos(int pos1, int pos2)
    {
        debug("Moving both servos to positions " + pos1 + " and " + pos2);
        if (pos1 <= 0 || pos1 >= 180 || pos2 <= 0 || pos2 >= 180)
            System.err.println("ERROR: moveBothServos positions must be greater than 0 and less than 180.  You supplied " + pos1 + " and " + pos2 + ".  One or more are invalid.");
        else
            sendRaw("V " + pos1 + " " + pos2);
    }
    public void moveStepper(int stepper, int steps)
    {
        debug("Moving stepper " + stepper + " " + steps + " steps");
        sendRaw("p " + stepper + " " + steps);
    }
    public void moveStepper(int stepper1, int steps1, int stepper2, int steps2)
    {
        debug("Moving steppers " + stepper1 + " and " + stepper2 + " to positions " + steps1 + " and " + steps2);
        sendRaw("P " + stepper1 + " " + steps1 + " " + stepper2 + " " + steps2);
    }
    public void runMotor(int motor, int speed, int time)
    {
        debug("Running motor " + motor + " at speed " + speed + " for time of " + time);
        sendRaw("d " + motor + " "  + speed + " " + time);
    }
    public void runMotor(int motor1, int speed1, int motor2, int speed2, int time)
    {
        debug("Running two motors, motor " + motor1 + " at speed " + speed1 + " and motor " + motor2 + " at speed " + speed2 + " for time of " + time);
        sendRaw("D " + motor1 + " " + speed1 +" " + motor2 + " " + speed2 + " " + time);
    }
}