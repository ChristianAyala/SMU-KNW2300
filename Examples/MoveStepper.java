import rxtxrobot.*;

public class MoveStepper{
    
    public static void main(String[] args)
    {    
        RXTXRobot r = new RXTXRobot("COM5"); //Create object on COM5

        r.moveStepper(RXTXRobot.STEPPER1, 48); //Run the stepper motor for 48 steps (2 revolutions). 
        
	// Nothing else can be executed while the stepper is turning
	
        r.close();
    }
}
