Windows:

	Plug in the Arduino board.  Windows 7 will not find the driver.  Install "Arduino UNO.inf" as the driver (found in this directory).  Now go into the folder that describes your computer's Java installation (32-bit vs 64-bit (SMU's computers have 64-bit)) and move "rxtxSerial.dll" to "\jre\bin\" (under whatever path your Java install is).  NOTE: It might be "\jdk\jre\bin\"
