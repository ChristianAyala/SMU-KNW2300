// This example shows how to move both servos simultaneously.  This is better than moving each one separately, as they move as close to simultaneously as possible.
import rxtxrobot.*;

public class MoveBothServos
{
	public static void main(String[] args)
	{
		RXTXRobot r = new RXTXRobot();  // Create RXTXRobot object 
		r.setPort("COM3"); // Set port to COM3
		r.moveBothServos(20,170); // Move Servo 1 to position 20 and Servo 2 to position 170 at the same time
		r.close();
	}
}
