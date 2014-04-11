/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rxtxrobot;

/**
 *
 * @author Chris
 */
public class Version implements Comparable {
    private int versionMajor;
    private int versionMinor;
    private int versionSubMinor;
    
    public Version(String version) 
    {
        try 
        {
            versionMajor = Integer.parseInt(version.substring(0, 1));
            if(version.length() == 3) 
            {
                versionMinor = Integer.parseInt(version.substring(2));
                versionSubMinor = 0;
            }
            else
            {
                versionMinor = Integer.parseInt(version.substring(2, 3));
                versionSubMinor = Integer.parseInt(version.substring(4));
            }
            
        } catch (Exception e) {
            versionMajor = versionMinor = versionSubMinor = 0;
        }
    }
    
    @Override
    public int compareTo(Object o) 
    {
        Version other = (Version)o;
        if (versionMajor > other.versionMajor) 
        {
            return 1;
        }
        else if (versionMajor < other.versionMajor)
        {
            return -1;
        }
        else
        {
            if (versionMinor > other.versionMinor)
            {
                return 1;
            }
            else if (versionMinor < other.versionMinor)
            {
                return -1;
            }
            else
            {
                if (versionSubMinor > other.versionSubMinor)
                {
                    return 1;
                }
                else if (versionSubMinor < other.versionSubMinor)
                {
                    return -1;
                }
                else
                {
                    return 0;
                }
            }
        }
    }
    
    @Override
    public String toString()
    {
        return "" + versionMajor + "." + versionMinor + "." + versionSubMinor;
    }
}
