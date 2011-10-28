Mac OS X:

Copy librxtxSerial.jnilib from the folder that is your operating system version to /Library/Java/Extensions

Tiger or earlier:
	Place "fixperm.sh" on to your Desktop.  Open up "Terminal" (found in Applications -> Utilities)  Type:
		cd ~/Desktop
		sudo sh ./fixperm.sh
		<enter your password>
Leopard or later:
	Place "fixperm-leopard.sh" on to your Desktop.  Open up "Terminal" (found in Applications -> Utilities)  Type:
		cd ~/Desktop
		sudo sh ./fixperm-leopard.sh
		<enter your password>


NOTE:  If the "fixperm" script displays an error, ask a TA for assistance.  I don't have a Mac to currectly test on, so I can't write this in too much detail.
