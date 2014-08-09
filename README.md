Instructions for Java API compilation and distribution:
=======
-------

Compilation
-----------
***  The "build" script referenced below only works in Linux, Unix, and Mac OS  **

The directory "RXTXRobot" is a valid NetBeans project.  Open it in NetBeans.


To compile it for students (JAR and Javadocs):

    Jar
    ------
    1) Click "Files" tab (typically on the left)
    2) Right click "build.xml" within the project files list
	3) Mouse over "Run Target"
	4) Mouse over "Other Targets"
	5) Click "Package-For-Store"
	6) Fix compilation errors if any occur and restart at #2
	
	Javadocs
	--------
	7) Open "RXTXRobot.java"
	8) Click "Run" in toolbar at the top of the screen
	9) Click "Generate Javadoc (RXTXRobot)"

Packaging
---------
To package it for the website:

	1) Follow instructions above to generate all the required files
	2) Make sure Perl is installed on the machine
	3) Run "build" script (no arguments will show the usage information)
		Example: ./build 4.0
	4) Fix errors if there are any and rerun #3

The zip files will be saved to your Desktop by default.

Distribution
------------
To upload it to the website:
	
	1) Open web browser to: http://lyle.smu.edu/fyd/admin.php
	2) Login
	3) Upload the JAR ZIP that is created in the above instructions
	4) Follow onscreen instructions
