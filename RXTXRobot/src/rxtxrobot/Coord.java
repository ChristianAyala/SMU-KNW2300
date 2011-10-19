package rxtxrobot;

public class Coord
{
    private double x;
    private double y;
    private double z;
    public Coord(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public double getX()
    {
        return x;        
    }
    public double getY()
    {
        return y;
    }
    public double getZ()
    {
        return z;
    }
    public void setX(double x)
    {
        this.x = x;
    }
    public void setY(double y)
    {
        this.y = y;
    }
    public void setZ(double z)
    {
        this.z = z;
    }
    @Override
    public String toString()
    {
        return "Coordinate (x,y,z): ("+getX()+","+getY()+","+getZ()+")";
    }
}