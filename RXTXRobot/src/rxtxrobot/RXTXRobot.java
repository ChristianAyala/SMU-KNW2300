package rxtxrobot;

/*  RXTXRobot API package
 *   
 *  All the methods in this package are asyncronous.  Therefore, if you move a motor for a certain amount
 *  of time, be sure to either Thread.sleep (or use the RXTXRobot.sleep method) or account for the execution
 *  time in your implementation
 * 
 * 
 *   public:
 *     +RXTXRobot(String port, [boolean verbose, [int bufferSize]])
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
 *     +moveStepper(int stepper_1, int steps_1, [int stepper_2, int steps_2])
 *     +runMotor(int motor_1, int speed_1, [int motor_2, int speed_2], int time)
 * 
 *   private:
 *     -debug(String str)
 */

//package rxtxrobot;
import gnu.io.*;
import java.io.*;

/**
 * 
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
     * Refers to the the M3 DC motor on the arduino
     */
    final public static int MOTOR1 = 2;
    /**
     * Refers to the M4 DC motor on the arduino
     */
    final public static int MOTOR2 = 3;
    /**
     * Refers to the M1/M2 Stepper motor on the arduino
     */
    final public static int STEPPER1 = 0;
    /**
     *
     */
    //final public static int STEPPER2 = 1;
    /**
     * The maximum number of digital pins that can be read from the arduino
     */
    final public static int NUM_DIGITAL_PINS = 12;
    /**
     * The maximum number of analog pins that can be read from the arduino
     */
    final public static int NUM_ANALOG_PINS = 6;
    /* Private variables */
    private String port;
    private boolean verbose;
    private OutputStream out;
    private InputStream in;
    private byte[] buffer;
    private String lastResponse;
    private SerialPort serialPort;
    private CommPort commPort;
    /**
     * Accepts a port number.
     * 
     * Accepts a port number, Sets the verbose
     * value to false and buffer size to 1024 by default. 
     * 
     * @param p Port number the arduino/xbee is connected to
     */
    public RXTXRobot(String p)
    {
        port = p;
        verbose = false;
        buffer = new byte[1024];
        connect();
    }
    /**
     * Accepts a port number and boolean value to turn on/off verbose messaging.
     * 
     * Accepts a port number and boolean value to turn on/off verbose messaging,
     * Sets the buffer size to 1024 by default.
     * 
     * @param p Port number the arduino/xbee is connected to
     * @param v Boolean value that allows for descriptive messaging
     */
    public RXTXRobot(String p, boolean v)
    {
        port = p;
        verbose = v;
        buffer = new byte[1024];
        connect();
    }
    /**
     * 
     * Attempts to connect to the arduino board.
     * 
     * This method identifies the port that is currently being used by the arduino 
     * and makes a serial connection to the arduino if the port is not already in use.
     * If there is an error in connecting, then a different error message will be 
     * displayed to the user for each case. Also opens up the input and output streams
     * so that they can be used.
     * 
     * 
     */
    public final void connect()
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
    /**
     * 
     * Checks if the arduino is connected.
     * 
     * Returns true if the serial port and comm port are not null which means that 
     * the arduino is connected. Returns false if the serial port or comm port are null
     * which means that the arduino is not connected.
     * 
     * @return true/false value that specifies if you are connected to the arduino
     */
    public final boolean isConnected()
    {
        return serialPort != null && commPort != null;
    }
    /**
     * 
     * Closes the connection to the arduino.
     * 
     * This method closes the serial connection and comm connection so that 
     * connection to the arduino is severed. This should be done before the program is 
     * exited/completed.
     * 
     */
    public final void close()
    {
        sleep(300);
        this.moveBothServos(90,90);
        if (serialPort != null)
            serialPort.close();
        if (commPort != null)
            commPort.close();
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
     * Sends the string in param s to the arduino to be executed.
     * 
     * If the output streams and input streams are open (a serial connection is present)
     * then it sends the string s to the arduino to be executed.  If verbose is true then 
     * the response from the arduino is read into lastResponse. If there is an IO exception
     * because writing cannot be done then an error is printed to the user.
     * 
     * @param s The command sent to the arduino 
     */
    public void sendRaw(String s)
    {
        debug("Sending command: " + s);
        try
        {
            if (out != null || in != null)
            {
                out.write((s+"\r\n").getBytes());
                if (verbose)
                {
                    buffer = new byte[1024];
                    sleep(200);
                    in.read(buffer, 0, Math.min(in.available(), buffer.length));
                    lastResponse = (new String(buffer)).trim();
                }
            }
        }
        catch(IOException e)
        {
            System.err.println("Cannot write command (IOException)! Error: " + e.getMessage());
        }
    }
    /**
     * 
     * Sends a command to labview to get the current LED values.
     * 
     * 
     * 
     * @param s String command that gets sent to lab view
     * @return the response from labview in string form
     */
     public Coord readFromLabView()
    {
        String command = "s";
        debug("Sending command: " + command);
        try
        {
            if (out != null || in != null)
            {
                
                buffer = new byte[1024];
                out.write((command).getBytes());
                sleep(800);
                in.read(buffer, 0, Math.min(in.available(), buffer.length));
                lastResponse = new String(buffer);
                debug("XBee Response: " + lastResponse);
                lastResponse = lastResponse.substring(lastResponse.indexOf("[")+1, lastResponse.indexOf("]"));
                String[] parts = lastResponse.split(",");
                debug("Creating Coord with x="+parts[0]+", y="+parts[1]+", z="+parts[2]);
                return new Coord(Double.parseDouble(parts[0]),Double.parseDouble(parts[1]),Double.parseDouble(parts[2]));
            }
        }
        catch(Exception e)
        {
            System.err.println("A generic error occurred: Error: " + e.getMessage());
            return null;
        }
        return null;
    }
    /**
     * 
     * Returns the last response sent from the arduino.
     * 
     * @return the last response sent from the arduino
     */
    public String getLastResponse()
    {
        return lastResponse;
    }
    /**
     * 
     * Allows the robot to sleep for time length measured in milliseconds.
     * 
     * Uses the Thread.sleep() function to put the robot to sleep for the specified
     * number of milliseconds. Throws an exception if the thread is interrupted during 
     * this process. (1000 milliseconds = 1 second)
     * 
     * @param length the amount of time in milliseconds
     */
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
    /**
     * 
     * Returns the 6 analog pin values from the arduino in String form.
     * 
     * Sends the command "r a" to the arduino to read the current analog pin values (6).
     * The values are put into last response which is then returned to where the method
     * was called. An error is displayed to the user if there is an error in reading 
     * the values from the arduino; in this case nothing is returned.
     * 
     * @return the output of the 6 analog pins in string form
     */
    public String getAnalogPins()
    {
        try
        {
            debug("Reading Analog Pins...");
            sendRaw("r a");
            sleep(200);
            in.read(buffer, 0, Math.min(in.available(), buffer.length));
            lastResponse = (new String(buffer)).trim();
            debug("Received response: " + lastResponse);
            return lastResponse;
        }
        catch(IOException e)
        {
            System.err.println("Cannot read command (IOException)! Error: " + e.getMessage());
        }
        return "";
    }
    /**
     * 
     * Returns the 12 digital pin values from the arduino.
     * 
     * Sends the command "r d" to the arduino to read the current digital pin values (12).
     * The values are put into last response which is then returned to where the method was
     * called. An error is displayed to the user if there is an error in reading the values
     * from the arduino; in this case nothing is returned.
     * 
     * @return the output of the 12 digital pins in string form
     */
    public String getDigitalPins()
    {
        try
        {
            debug("Reading Digital Pins...");
            sendRaw("r d");
            sleep(200);
            in.read(buffer, 0, Math.min(in.available(), buffer.length));
            lastResponse = (new String(buffer)).trim();
            debug("Received response: " + lastResponse);
            return lastResponse;
        }
        catch(IOException e)
        {
            System.err.println("Cannot read command (IOException)! Error: " + e.getMessage());
        }
        return "";
    }
    /**
     * 
     * Returns the value of the digital pin specified by the index param.
     * 
     * Calls the {@link getDigitalPins()} method to get the values for all of the arduino
     * digital pins. It converts the String to an array of Strings with one of the pin values
     * in each position of the array. The index of an element within the array corresponds to the pin number
     * on the arduino board. If the index is inside the bounds of the array then the value at that 
     * position is parsed into an int which is then returned to where the method was called.
     * If the position was not within the bounds of the array then an error message is displayed to
     * the user and a -1 value is returned.
     * 
     * @param index the pin number that you want to read from
     * @return the value of the specified pin number
     */
    public int getDigitalPin(int index)
    {
        String pins = this.getDigitalPins();
        String[] split = pins.split("\\s+");
        if (index >=RXTXRobot.NUM_DIGITAL_PINS || index<0)
            System.err.println("ERROR: getDigitalPin was given an index that is not within the range of 0 to "+(RXTXRobot.NUM_DIGITAL_PINS-1)+" (inclusive)");
        else
            return Integer.parseInt(split[index+1]);
        return -1;
    }
    /**
     * 
     * Returns the value of the analog pin number specified by the index param.
     * 
     * Calls the {@link getAnalogPins()} method to get the values for all of the arduino
     * analog pins. It converts the String to an array of Strings with one of the pin
     * values in each position of the array. The index of an element within the array corresponds 
     * to the pin number on the arduino board. If the index is inside the bounds of the array then the 
     * value at that position is parsed into an int which is then returned to where the meothd was called. 
     * If the position is not within the bounds of the array then an error message is displayed to 
     * the user and a -1 value is returned.
     * 
     * @param index the pint number that you want to read from
     * @return the value of the specified pin number
     */
    public int getAnalogPin(int index)
    {
        String pins = this.getAnalogPins();
        String[] split = pins.split("\\s+");
        if (index >=RXTXRobot.NUM_ANALOG_PINS || index<0)
            System.err.println("ERROR: getAnalogPin was given an index that is not within the range of 0 to "+(RXTXRobot.NUM_ANALOG_PINS-1)+" (inclusive)");
        else
            return Integer.parseInt(split[index+1]);
        return -1;
    }
    /**
     * 
     * Allows you to set the value of the specified analog pin.
     * 
     * Sends a command to the arduino to set the value of a specified
     * analog pin to the value the user passes the method.
     * 
     * @param pin the analog pin number that you would like to access
     * @param value the value that you would like to set the specified pin to
     */
    public void setAnalogPin(int pin, int value)
    {
        debug("Setting Analog Pin " + pin + " to " + value);
        sendRaw("w a " + pin + " " + value);
    }
    /**
     * 
     * Allows you to set the value of the digital pin.
     * 
     * Sends a command to the arduino to stt the value of a specified
     * digital pin to the value the user passes the method.
     * 
     * @param pin the analog pin number that you would like to access
     * @param value the value that you would like to set the specified pin to
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
     * If the parameters are acceptable values then the command will be sent to the arduino to move the servo specified
     * the specified number of degrees.  If the servo passed is not RXTXRobot.SERVO1 or RXTXRobot.SERVO2
     * then an error message will be displayed to the user and the command will not be passed to the arduino.
     * If the angular position is not between 0 and 180 then an error message will be displayed to the user 
     * and the command will not be passed to the arduino.
     * 
     * @param servo the servo motor that you would like to move: RXTXRobot.SERVO1 or RXTXRobot.SERVO2.
     * @param position the position (in degrees) where you want the servo to turn to: 0 < position > 180.
     */
    public void moveServo(int servo, int position)
    {
        if (servo != RXTXRobot.SERVO1 && servo != RXTXRobot.SERVO2)
        {
            System.err.println("ERROR: moveServo: Invalid servo argument");
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
     * Accepts to angular positions between 0 and 180 exclusive and moves the servo 
     * motors to the corresponding angular position. SERVO1 moves pos1 degrees and 
     * SERVO2 moves pos2 degrees.  If either pos1 or pos2 is not between 0 and 180
     * then an error message will be displayed to the user and the command will not 
     * be sent.
     * 
     * @param pos1 the angular position of servo 1
     * @param pos2 the angular position of servo 2
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
     * Move one of the stepper motors a specified number of steps.
     * 
     * Accepts a stepper motor (M1 and M2 on the ardiuno board) and a number of steps
     * that the stepper motor should turn.  The command will be sent to the arduino to 
     * turn the specified motor the specified number of steps. Negative steps will go
     * counter clockwise and positive steps will rotate the motor clockwise. 
     * One step = 15 degrees. i.e. 24 steps for one full revolution
     * 
     * @param stepper the stepper motor that you want to move
     * @param steps the number of steps you want the motor to move
     */
    public void moveStepper(int stepper, int steps)
    {
        if(stepper != RXTXRobot.STEPPER1){
            System.err.println("ERROR: moveStepper was not given the correct stepper argument");
            return;
        }
        debug("Moving stepper " + stepper + " " + steps + " steps");
        sendRaw("p " + stepper + " " + steps);
        sleep((int)(steps*60*1000*((24.0/100))/(24*30)));
    }
    /**
     * 
     * Move both stepper motors a specified number of steps.
     * 
     * Accepts both stepper motors (M1 or M2 on the arduino board) and a specified number of steps for each motor. 
     * The command will be sent to the arduino to turn each motor the specified number of steps simultaneously. 
     * Negative steps will go counter clockwise and positive steps will rotate the motor clockwise.
     * One step = 15 degrees. i.e. 24 steps for one full revolution
     * 
     * @param stepper1 stepper motor 1
     * @param steps1 the number of steps motor 1 should move
     * @param stepper2 stepper motor 2 
     * @param steps2 the number of steps motor 2 should move
     */
    public void moveStepper(int stepper1, int steps1, int stepper2, int steps2)
    {
        
        //if((stepper1 != RXTXRobot.STEPPER1 && stepper2 != RXTXRobot.STEPPER2) || (stepper2 != RXTXRobot.STEPPER1 && stepper1 != RXTXRobot.STEPPER2) ){
        //    System.out.println("ERROR: moveStepper was not given a correct stepper argument");
        //}
        debug("Moving steppers " + stepper1 + " and " + stepper2 + " to positions " + steps1 + " and " + steps2);
        sendRaw("P " + stepper1 + " " + steps1 + " " + stepper2 + " " + steps2);
        if(steps1 > steps2){
            sleep((int)(steps1*60*1000*((24.0/100))/(24*30)));
        }
        else{
            sleep((int)(steps2*60*1000*((24.0/100))/(24*30)));
        }
    }
    /**
     * 
     * Runs the specified motor the specified speed for the specified time.
     *   
     * Accepts a DC motor, either RXTXRobot.MOTOR1 or RXTXRobot.MOTOR2, the speed 
     * that the motor should run at, and the time with which the motor should run. If the
     * parameters are acceptable values then the command to run the motors will be sent
     * to the arduino. If the incorrect motor is passed to the function then an error message will be 
     * displayed to the user and the command will not be sent.
     * 
     * @param motor the DC motor you want to run: RXTXRobot.MOTOR1 or RXTXRobot.MOTOR2
     * @param speed the speed that the motor should run at
     * @param time the amount of time the motor should run in milliseconds
     */
    public void runMotor(int motor, int speed, int time)
    {
        if (motor != RXTXRobot.MOTOR1 && motor != RXTXRobot.MOTOR2)
        {
            System.err.println("ERROR: runMotor was not given a correct motor argument");
            return;
        }
        debug("Running motor " + motor + " at speed " + speed + " for time of " + time);
        sendRaw("d " + motor + " "  + speed + " " + time);
        sleep(time);
    }
    /**
     * 
     * Runs both DC motors at uniquely specified speeds for a specified amount of time.
     * 
     * Accepts both DC motors, either RXTXRobot.MOTOR1 or RXTXRobot.MOTOR2, the speeds 
     * for each motor and the amount of time that the motors should run for. If the parameters
     * are acceptable values then the command will be sent for both motors to run simultaneously.
     * If the incorrect motor is passed to the function then an error message will be displayed to the user
     * and the command will not be sent.
     *  
     * 
     * @param motor1 the first DC motor: MOTOR1 or MOTOR2
     * @param speed1 the speed that the first DC motor should run at
     * @param motor2 the second DC motor: MOTOR1 or MOTOR2
     * @param speed2 the speed that the second DC motor should run at
     * @param time the amount of time that the DC motors should run
     */
    public void runMotor(int motor1, int speed1, int motor2, int speed2, int time)
    {
        if ((motor1 != RXTXRobot.MOTOR1 && motor1 != RXTXRobot.MOTOR2) || (motor2 != RXTXRobot.MOTOR1 && motor2 != RXTXRobot.MOTOR2))
        {
            System.err.println("ERROR: runMotor was not given a correct motor argument");
            return;
        }
        debug("Running two motors, motor " + motor1 + " at speed " + speed1 + " and motor " + motor2 + " at speed " + speed2 + " for time of " + time);
        sendRaw("D " + motor1 + " " + speed1 +" " + motor2 + " " + speed2 + " " + time);
        sleep(time);
    }
}