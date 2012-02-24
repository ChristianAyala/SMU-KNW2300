import rxtxrobot.*;

public class MoveStepper{
    
    public static void main(String[] args){
        
        RXTXRobot r = new RXTXRobot("COM5"); //Create object on COM5\
        //r.moveStepper(stepper motor, # of steps);
        r.moveStepper(RXTXRobot.STEPPER1, 48); //Run the stepper motor where it does 48 steps (2 revolutions). 
        //24 steps = 360 degrees for a properly calibrated stepper motor and arduino
        //nothing else can be executed while the stepper is turning
        r.close();
    }
}