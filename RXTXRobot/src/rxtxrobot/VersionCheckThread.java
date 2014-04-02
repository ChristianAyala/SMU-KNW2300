package rxtxrobot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 
 * Thread class to check the most recent version of the API by using the FYD site
 * @author Chris Ayala
 */
public class VersionCheckThread extends Thread 
{
        private static final int TIMEOUT = 2*1000;
        private static final String VERSION_URL = "http://lyle.smu.edu/fyd/query.php?action=version";
    
        @Override
        public void run()
        {
                try
                {
                        //Open the connection
                        URLConnection connection = new URL(VERSION_URL).openConnection();
                        connection.setReadTimeout(TIMEOUT);
                        connection.setConnectTimeout(TIMEOUT);
                        
                        //Read the response
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder builder = new StringBuilder();
                        while ((inputLine = in.readLine()) != null)
                        {
                                builder.append(inputLine);
                        }
                        String onlineVersion = builder.toString();
                        Global.debug("Most recent version from website: " + onlineVersion);
                        //Do the comparison
                        if (!Global.getVersion().equals(onlineVersion))
                        {
                                Global.error("There is an updated version of this API (version " + onlineVersion + ")" +
                                                   "\nat lyle.smu.edu/fyd/downloads.php", "VersionCheck", "run", false);
                        }
                        else
                        {
                                Global.debug("You are on the most up-to-date version of the API! (version " + onlineVersion + ")");
                        }
                }
                catch (MalformedURLException e) 
                {
                        Global.debug("VersionCheck - Malformed URL Exception: " + VERSION_URL);
                }
                catch (SocketTimeoutException e)
                {
                        Global.debug("VersionCheck - Socket timeout after " + TIMEOUT + " ms, failed to connect to the version check page");
                }
                catch (IOException e)
                {
                        Global.debug("VersionCheck - IOException - Could not connect to the version check server");
                }
                catch (Exception e)
                {
                        Global.debug("VersionCheck - General Exception");
                }
        }
}