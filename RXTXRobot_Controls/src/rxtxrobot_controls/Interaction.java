package rxtxrobot_controls;

import java.awt.Color;
import java.io.PrintStream;
import java.util.ArrayList;
import rxtxrobot.AnalogPin;
import rxtxrobot.RXTXRobot;

public final class Interaction implements Runnable
{
    public static final int RUN_MOTOR = 1;
    public static final int MOVE_SERVO = 2;
    public static final int MOVE_BOTH_SERVOS = 5;
    public static final int READ_ANALOG = 3;
    public static final int READ_DIGITAL = 4;
    private RXTXRobot robot;
    private boolean running;
    private String port;
    private PrintStream out;
    private PrintStream err;
    private ArrayList<int[]> execArgs;
    private MainWindow parent;    
    public Interaction(MainWindow par, String p, PrintStream out, PrintStream err)
    {
        parent = par;
        port = p;
        this.out = out;
        this.err = err;
        execArgs = new ArrayList<int[]>();
    }
    
    @Override
    public void run()
    {
        running = true;
        this.connect();
        while (running)
        {
            synchronized(this)
            {
                if (execArgs.isEmpty())
                try
                {
                    this.wait();
                }
                catch (InterruptedException ex)
                {
                }

                if (!execArgs.isEmpty())
                {
                    try
                    {
                        int[] exec = execArgs.remove(0);
                        switch(exec[0])
                        {
                            case Interaction.MOVE_SERVO:
                                robot.moveServo(exec[1], exec[2]);
                                break;
                            case Interaction.MOVE_BOTH_SERVOS:
                                robot.moveBothServos(exec[1],exec[2]);
                                break;
                            case Interaction.RUN_MOTOR:
                                robot.runMotor(exec[1],exec[2], exec[3]);
                                break;
                            case Interaction.READ_ANALOG:
                                robot.refreshAnalogPins();
                                String set = "";
                                for (int x=0;x < RXTXRobot.NUM_ANALOG_PINS;++x)
                                {
                                    if (x != 0)
                                        set += ", ";
                                    set += robot.getAnalogPin(x).getValue();
                                }
                                parent.analog_textbox.setText(set);
                                break;
                            case Interaction.READ_DIGITAL:
                                robot.refreshDigitalPins();
                                String set1 = robot.getDigitalPin(2).getValue() + ", " + robot.getDigitalPin(4).getValue() + ", " + robot.getDigitalPin(7).getValue() + ", " + robot.getDigitalPin(8).getValue() + ", " + robot.getDigitalPin(12).getValue() + ", " + robot.getDigitalPin(13).getValue();
                                parent.digital_textbox.setText(set1);
                                break;
                            default:
                                break;
                        }
                    }
                    catch(Exception e)
                    {
                        System.err.println("An error occured with executing command");
                    }
                }
            }
        }
        disconnect();
    }
    public void execute(int... args)
    {
        execArgs.add(args);
    }
    private void connect()
    {
        parent.arduino_connect_btn.setText("Connecting");
        parent.connection_status.setText("Connecting...");
        parent.connection_status.setForeground(new Color(0,0,0));
        parent.arduino_connect_btn.setEnabled(false);
        robot = new RXTXRobot(port,true);
        robot.setErrStream(err);
        robot.setOutStream(out);
        if (robot.isConnected())
        {
            parent.arduino_connect_btn.setText("Disconnect");
            parent.arduino_connect_btn.setEnabled(true);
            parent.connection_status.setText("Connected");
            parent.connection_status.setForeground(new Color(0,190,0));
            parent.enableAll(true);
        }
        else
        {
            parent.arduino_connect_btn.setText("Connect");
            parent.arduino_connect_btn.setEnabled(true);
            parent.connection_status.setText("Error");
            parent.connection_status.setForeground(new Color(255,0,0));
            parent.enableAll(false);
        }
    }
    public void stopRunning()
    {
        running = false;
    }
    public void disconnect()
    {
        parent.arduino_connect_btn.setText("Connect");
        parent.arduino_connect_btn.setEnabled(true);
        parent.connection_status.setText("Disconnected");
        parent.connection_status.setForeground(new Color(255,0,0));
        robot.close();
        robot = null;
        parent.enableAll(false);
    }
    public RXTXRobot getRXTXRobot()
    {
        return robot;
    }
}
