@ECHO OFF
set save=%cd%

CD "C:\Program Files\Java\"
FOR /D /r %%i IN (bin*) DO ( COPY "%save%\rxtxSerial.dll" "%%i" >nul )
