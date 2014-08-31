RXTXRobot Library Instructions
==============================

NOTE: If you are running Windows 8 or 8.1, read the following section. Otherwise, continue
to the Installation section.

Windows users:
	Windows 8 and above does not allow unsigned driver installation by default.
	In order to allow unsigned drivers, perform the following steps:

	1) Use the Windows + I keyboard shortcut to open the Settings charm.

	2) If using Windows 8:
		2.1) Click the General option on the left side of the screen
		2.2) Under Advanced Startup at the very bottom, click Restart Now
	   If using Windows 8.1:
		2.1) Click the Update and Recovery option on the left side of the screen
		2.2) Click the Recovery option on the left side of the screen
		2.3) Under Advanced Startup, click Restart Now
	
	3) Once it has restarted, click Troubleshoot.
	
	4) Click Advanced Options

	5) Click Startup Settings

	6) Click Restart

	7) Once it has restarted, type the number for “Disable driver signature enforcement”

	Your computer will then boot normally. Continue to the Installation section. After
	installation, restart your computer. Driver signature enforcement will be re-enabled
	automatically.

Installation:

	1) Go into the "INSTALL" folder

	2) Go into the folder for Windows, Mac OS X, or Linux

	3) Double click the "install" program and follow any onscreen instructions.

	4) Go to http://lyle.smu.edu/fyd/downloads.php, and download the latest stable
	   release of the RXTXRobot JAR.

	5) Add the JAR to your IDE project.

	6) Add "import rxtxrobot.*" at the top of your Java files.

Javadocs:
	To read the Javadocs, go into the "javadoc" folder and 
	double click the file named "index.html".  It should 
	open up in a web browser and you can now read about the API.



