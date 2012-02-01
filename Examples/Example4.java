public class MoveBothServos
{
	public static void main(String[] args)
	{
		RXTXRobot r = new RXTXRobot("COM3");  // Create object on COM3
		r.moveBothServos(20,170); // Move servo 1 to position 20, servo 2 to position 170
		r.close();
	}
}
