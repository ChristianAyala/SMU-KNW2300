@ECHO OFF
set save=%cd%

CD "C:\Program Files (x86)\Java\"
FOR /D /r %%i IN (bin*) DO ( COPY  "%save%\32\rxtxSerial.dll" "%%i" >nul )

CD "C:\Program Files\Java\"
FOR /D /r %%i IN (bin*) DO ( COPY "%save%\64\rxtxSerial.dll" "%%i" >nul )
