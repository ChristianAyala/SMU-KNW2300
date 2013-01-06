import rxtxrobot.*;

public class MoveServo
{
	public static void main(String[] args)
	{
		RXTXRobot r = new RXTXRobot(); // Create RXTXRobot object
		r.setPort("COM3"); // Set the port to COM3
		r.setVerbose(true); // Turn on debugging messages
		r.moveServo(RXTXRobot.SERVO1, 30); // Move Servo 1 to location 30
		r.moveServo(RXTXRobot.SERVO2, 170); // Move Servo 2 to location 170
		r.close();
	}
}

