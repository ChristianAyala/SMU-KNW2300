import rxtxrobot.*;

public class RunOneMotor
{
	public static void main(String[] args)
	{
		RXTXRobot r = new RXTXRobot("COM3"); // Create object using COM3
		r.runMotor(RXTXRobot.MOTOR1, 125, 5000); // Run motor 1 forward (speed of 125) for 5 seconds
		// Thread blocks until above command is done
		r.runMotor(RXTXRobot.MOTOR1, -125, 3000); // Run motor 1 backward (speed of 125) for 3 seconds
		r.close();
	}
}
