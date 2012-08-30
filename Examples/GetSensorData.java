import rxtxrobot.*;

public class GetSensorData
{   
    public static void main(String[] args)
    {    
        // All sensor data will be read from the analog pins
	
        RXTXRobot r = new RXTXRobot("COM5"); //Create object on COM5

        //Create a temporary integer array to capture the sensor data
        int[] temp = r.getAnalogPins();

        // Read the data from each array position and print out the value.
        // Array positions will be 0-5 corresponding to the analog pin numbers
        for(int x = 0; x < temp.length; x++)
	{
            System.out.println("Sensor " + (x+1) + " value: " + temp[x]);
        }
        //you can also access each singular position in the array. 
        System.out.println("Sensor 1 value: " + temp[0]);
        System.out.println("Sensor 2 value: " + temp[1]);
        System.out.println("Sensor 3 value: " + temp[2]);
        System.out.println("Sensor 4 value: " + temp[3]);
        System.out.println("Sensor 5 value: " + temp[4]);
        System.out.println("Sensor 6 value: " + temp[5]);
        
        r.close();
    }
}
