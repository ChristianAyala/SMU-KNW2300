@ECHO OFF
set save=%cd%

CD "C:\Program Files\Java\"
FOR /D /r %%i IN (bin*) DO ( DEL "%%i\rxtxSerial.dll" >nul )
