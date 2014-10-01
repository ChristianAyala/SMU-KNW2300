package rxtxrobot;

import java.io.PrintStream;

/**
 * Class with common functionality and "global" data accessible to the entire
 * API.
 *
 */
public class Global {
        /**
         * Global major version.
         * 
         * Update this for significant API/firmware rewrites. If this does
         * not match the major version of the firmware, the communication with
         * the Arduino will likely not work.
         */
        final protected static int VERSION_MAJOR = 4;
        
        /**
         * Global minor version.
         * 
         * Update this for regular API updates that required a corresponding
         * firmware change to the Arduino. If this is ahead of the minor version
         * of the firmware, then new expected functionality will likely not work.
         * If this is behind the minor version of the firmware, then new hardware
         * functionality will not be accessible from this API.
         */
        final protected static int VERSION_MINOR = 2;
        
        /**
         * Global subminor version.
         * 
         * Update this for bug fixes and minor changes that did not
         * require updates to the firmware. It should have no effect on the
         * functionality of the API if this does not match the firmware subminor
         * version.
         */
        final protected static int VERSION_SUBMINOR = 0;
        private static PrintStream out_stream;
        private static PrintStream err_stream;
        
        public static void setOutStream(PrintStream o)
        {
                out_stream = o;
        }
        
        public static PrintStream getOutStream()
        {
                return out_stream;
        }
        
        public static void setErrStream(PrintStream e)
        {
                err_stream = e;
        }
        
        public static PrintStream getErrStream()
        {
                return err_stream;
        }
        
        public static Version getVersion()
        {
                return new Version("" + VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_SUBMINOR);
        }
        
        public static void debug(String str)
        {
                getOutStream().println("--> " + str);
                getOutStream().flush();
        }
        
        public static void error(String str, String class_name, String method, boolean fatal)
        {
                if (fatal)
                        getErrStream().println("FATAL ERROR (method: " + method + "): ");
                else
                        getErrStream().println("ERROR (method: " + method + " ): ");
                getErrStream().println("Message:");
                str = str.replaceAll("\\n", "\n\t");
                getErrStream().println("\t" + str);
                getErrStream().flush();
        }
}
