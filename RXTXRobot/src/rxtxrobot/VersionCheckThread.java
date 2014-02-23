/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author Chris
 */
public class VersionCheckThread extends Thread 
{
        private static final int TIMEOUT = 5*1000;
        private String expectedVersion;
        
        public VersionCheckThread(String expectedVersion)
        {
                this.expectedVersion = expectedVersion;
        }
    
        @Override
        public void run()
        {
                String url = "http://lyle.smu.edu/fyd/query.php?action=version";
                try
                {
                        URLConnection connection = new URL(url).openConnection();
                        connection.setReadTimeout(TIMEOUT);
                        connection.setConnectTimeout(TIMEOUT);
                        
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder builder = new StringBuilder();
                        while ((inputLine = in.readLine()) != null)
                        {
                                builder.append(inputLine);
                        }
                        String onlineVersion = builder.toString();
                        if (!expectedVersion.equals(onlineVersion))
                        {
                                System.err.println("There is an updated version of this API (version " + onlineVersion + ")" +
                                                   "\nat lyle.smu.edu/fyd/downloads.php");
                        }
                        else
                        {
                                System.out.println("You are on the most up-to-date version of the API! (version " + onlineVersion + ")");
                        }
                }
                catch (MalformedURLException e) 
                {
                        System.err.println("Malformed URL Exception: " + url);
                }
                catch (SocketTimeoutException e)
                {
                        System.err.println("Socket timeout after " + TIMEOUT + " ms, failed to connect to the version check page");
                }
                catch (IOException e)
                {
                        System.err.println("IOException");
                }
                catch (Exception e)
                {
                        System.err.println("General Exception");
                }
        }
}
