Linux:

Setting up this library on Linux is a multi-step process.  Firstly, you must determine what architecture your hardware/OS is compatible with.  To do this, run the following command in terminal:

	uname -a | grep -o -P [^\.]i[1-9]{3}

The above command lists the possible architectures that your operating system can support (most likely, you will have i686/i386).  Find the corresponding architecture folder within the RXTXComm_INSTALL/Linux folder (if you can't find one, use the i686 folder).

Within this folder, you will find a file named "librxtxSerial.so".  You must move (or copy) that file into whereever your Java JRE is installed.  For example, your JRE install might be in "/usr/lib/jvm/java-#####/jre/" (where ##### is the version number).  You can check Netbeans or Eclipse to find out which environment it is using.  Once you find where the JRE is installed, you must copy the "librxtxSerial.so" to the jre/lib/[machine type] folder.  So, for example, you would put the file into "/usr/lib/jvm/java-#####/jre/lib/i386", but changing ##### to the Java version type, and i386 to whichever architecture folder ALREADY EXISTS inside the "lib" folder.  NOTE: The architectures may not be the same for the folder that you got the librxtxSerial.so file and the jre/lib folder.  That is normal.  Here is an example command that would do all this (assuming terminal is open in "RXTXComm_INSTALL/Linux/[machine_type]" and the folders are correct:

	cp librxtxSerial.so /usr/lib/jvm/java-1.6.0-openjdk/jre/lib/i386/

Next, you must ensure that your user is in the usergroup "lock", "uucp", and "dialout".  You can either do this through the graphical menus (probably under "System Settings" -> "User and Groups", but it depends on your distribution), or you can do it through command line (NOTE: YOU MUST RESTART YOUR COMPUTER AFTER DOING THIS STEP, for both GUI and command line):

	usermod -a -G lock,uucp,dialout [username]		#(Where [username] is your username)

This command may not exist on your distribution, but try it and if you get an error, you might have to Google how to do this.

Lastly, RXTXComm uses the /lock folder for the mutual exclusion control of the serial port.  This folder doesn't exist on a lot of distributions (it exists, but elsewhere on the system).  To fix this, you must run the following command in terminal:

	sudo mkdir /lock && sudo chmod 777 /lock

That command will ask you for your password.


That should be it.  Add RXTXComm.jar to your list of libraries in Netbeans or Eclipse, and you will have access to the Arduino API.



NOTE:

If you get errors about "unable to obtain lock" or something similar (maybe about "uucp_lock"), open terminal and run the command:

	sudo chmod -R 777 /lock


