// This example shows how to move both servos simultaneously.  This is better than moving each one separately, as they move as close to simultaneously as possible.
import rxtxrobot.*;

public class MoveBothServos
{
	public static void main(String[] args)
	{
		RXTXRobot r = new ArduinoUno();  // Create RXTXRobot object
		r.setPort("COM3"); // Set port to COM3
		r.connect();
		r.attachServo(RXTXRobot.SERVO1); //Connect the servos to the Arduino
		r.attachServo(RXTXRobot.SERVO2);
		r.attachServo(RXTXRobot.SERVO3);
		r.moveAllServos(20,170, 0); // Move Servo 1 to position 20, Servo 2 to position 170, and Servo 3 to position 0.
		r.close();
	}
}
