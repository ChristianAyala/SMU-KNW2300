/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rxtxrobot;

import java.io.PrintStream;

/**
 *
 * @author Chris
 */
public class Globals {
    
        public static final String API_VERSION = "3.4";
        private static PrintStream out_stream;
        private static PrintStream err_stream;
        private static boolean verbose;
        
        /**
         * Sets the output PrintStream; System.out by default.
         *
         * This method lets you set the output stream from System.out to
         * somewhere else. It shouldn't be used unless you know what you are
         * doing.
         *
         * @param o PrintStream to write output to.
         */
        public static void setOutStream(PrintStream o)
        {
                out_stream = o;
        }

        /**
         * Gets the output PrintStream; System.out by default.
         *
         * This method returns the output stream that is being used.
         *
         * @return PrintStream that is being used for output
         */
        public static PrintStream getOutStream()
        {
                return out_stream;
        }

        /**
         * Sets the error PrintStream; System.err by default.
         *
         * This method lets you set the error stream from System.err to
         * somewhere else. It shouldn't be used unless you know what you are
         * doing.
         *
         * @param e PrintStream to write error to.
         */
        public static void setErrStream(PrintStream e)
        {
                err_stream = e;
        }

        /**
         * Gets the error PrintStream; System.err by default.
         *
         * This method returns the error stream that is being used.
         *
         * @return PrintStream that is being used for error output
         */
        public static PrintStream getErrStream()
        {
                return err_stream;
        }
        
        /**
         * Sets verbose output.
         *
         * Setting verbose output allows for easier debugging and seeing what is
         * happening behind the scenes. Turn this on to debug your code.
         *
         * @param v Boolean to set verbosity.
         */
        public void setVerbose(boolean v)
        {
                verbose = v;
        }

        /**
         * Gets the verbose setting.
         *
         * @return The boolean for the verbose setting.
         */
        public boolean getVerbose()
        {
                return verbose;
        }
        
        /**
         * Prints out debugging statements.
         *
         * If verbose is set to true, then debugging statements will be printed
         * out to the user. If verbose is set to false, then these statements
         * will not print.
         *
         * @param str Message passed to debug
         */
        protected static void debug(String str)
        {
                if (verbose)
                        getOutStream().println("--> " + str);
        }
        
        /**
         * Prints out an error message to the console.
         *
         * Prints the string to the screen in a unified format.
         *
         * @param str String to print to the screen.
         */
        protected void error(String str)
        {
                error(str, "SerialCommunication", "N/A");
        }

        /**
         * Prints out an error message to the console.
         *
         * Prints the string to the screen, giving more information such as
         * class name and method name.
         *
         * @param str String to print to the screen.
         * @param class_name Name of the class where the error originated.
         * @param method Name of the method where the error originated.
         */
        protected void error(String str, String class_name, String method)
        {
                error(str, class_name, method, false);
        }

        /**
         * Print out an error message to the console.
         *
         * Prints the string to the screen, giving more information such as
         * class name and method name, and whether the error is fatal or not.
         *
         * @param str String to print to the screen
         * @param class_name Name of the class where the error originated.
         * @param method Name of the method where the error originated.
         * @param fatal Boolean specifying whether the error is fatal.
         */
        protected void error(String str, String class_name, String method, boolean fatal)
        {
                if (fatal)
                        getErrStream().println("FATAL ERROR (method: " + method + "):");
                else
                        getErrStream().println("ERROR (method: " + method + " ): ");
                getErrStream().println("Message:");
                str = str.replaceAll("\\n", "\n\t");
                getErrStream().println("\t" + str);
        }
}
