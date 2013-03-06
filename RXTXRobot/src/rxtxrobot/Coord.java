package rxtxrobot;

/**
 * @author Chris King
 * @version 3.1.1
 */
public class Coord
{
        private double x;
        private double y;
        private double z;

        /**
         * Accepts an x, y, and z coordinate
         *
         * @param x The x-coordinate
         * @param y The y-coordinate
         * @param z The z-coordinate
         */
        public Coord(double x, double y, double z)
        {
                this.x = x;
                this.y = y;
                this.z = z;
        }

        /**
         * Returns the x-coordinate
         *
         * @return Returns the x-coordinate
         */
        public double getX()
        {
                return x;
        }

        /**
         * Returns the y-coordinate
         *
         * @return Returns the y-coordinate
         */
        public double getY()
        {
                return y;
        }

        /**
         * Returns the z-coordinate
         *
         * @return Returns the z-coordinate
         */
        public double getZ()
        {
                return z;
        }

        /**
         * Sets the x-coordinate
         *
         * @param x The x-coordinate to set the Coord's x-coordinate
         */
        public void setX(double x)
        {
                this.x = x;
        }

        /**
         * Sets the y-coordinate
         *
         * @param y The y-coordinate to set the Coord's y-coordinate
         */
        public void setY(double y)
        {
                this.y = y;
        }

        /**
         * Sets the z-coordinate
         *
         * @param z The z-coordinate to set the Coord's z-coordinate
         */
        public void setZ(double z)
        {
                this.z = z;
        }

        /**
         * Displays the coordinate in a readable form
         *
         * @return A string that is a readable representation of the Coord
         * object
         */
        @Override
        public String toString()
        {
                return "Coordinate (x,y,z): (" + getX() + "," + getY() + "," + getZ() + ")";
        }
}