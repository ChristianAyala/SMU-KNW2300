import rxtxrobot.*;

public class MoveBothServos
{
	public static void main(String[] args)
	{
		RXTXRobot r = new RXTXRobot("COM3");  // Create object on COM3
		r.moveBothServos(20,170); // Move Servo 1 to position 20 and Servo 2 to position 170 at the same time
		r.close();
	}
}
