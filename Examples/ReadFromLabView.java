import rxtxrobot.*;

public class ReadFromLabView{
    
    public static void main(String[] args){
        
        RXTXRobot r = new RXTXRobot("COM5");//Create object on COM5
        //if the wiimotes return 3D coordinates then readfromLabView3D() will return a Coord Object
        Coord c = r.readFromLabView3D();
        //use the Coord accessor methods to get the data for the coordinate values from labview
        System.out.println("X: " + c.getX());
        System.out.println("Y: " + c.getY());
        System.out.println("Z: " + c.getZ());
        //if the wiimotes only return 2D coordinates then readFromLabView2D() will return an array of Coord Objects of size 2
        Coord[] c1 = r.readFromLabView2D();
        //access the values by specifying the array position then the accessor methods 
        System.out.println("X1: " + c1[0].getX());
        System.out.println("Y1: " + c1[0].getY());
        System.out.println("X2: " + c1[1].getX());
        System.out.println("Y2: " + c1[1].getY());
    }
}