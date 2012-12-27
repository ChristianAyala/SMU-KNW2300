@ECHO OFF
set javafound=n
set save=%cd%
set sixtybit=y
ECHO ^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=
ECHO ^|   RXTXRobot Installer for Windows   ^|
ECHO ^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=

ECHO Installing Java Libraries..........................................
IF NOT exist %save%\libs\32-bit\rxtxSerial.dll goto fatalerror
IF NOT exist %save%\libs\64-bit\rxtxSerial.dll goto fatalerror
IF exist "C:\Program Files (x86)\" goto checkOldBits
goto checkRegularNo64


:checkOldBits
IF exist "C:\Program Files (x86)\Java\" goto doOldBits
goto checkRegular

:doOldBits
CD "C:\Program Files (x86)\Java\"
FOR /D /r %%i IN (bin*) DO ( COPY  %save%\libs\32-bit\rxtxSerial.dll %%i >nul )
set javafound=y
goto checkRegular


:checkRegular
IF exist "C:\Program Files\Java\" goto doRegular
goto closing

:doRegular
CD "C:\Program Files\Java\"
FOR /D /r %%i IN (bin*) DO ( COPY %save%\libs\64-bit\rxtxSerial.dll %%i >nul )
set javafound=y
goto closing

:checkRegularNo64
set sixtybit=n
IF exist "C:\Program Files\Java\" goto doRegularNo64
goto closing

:doRegularNo64
CD "C:\Program Files\Java\"
FOR /D /r %%i IN (bin*) DO ( COPY %save%\libs\32-bit\rxtxSerial.dll %%i >nul )
set javafound=y
goto closing



:fatalerror
ECHO FATAL ERROR:
ECHO The required libraries were not found!  You must have:
ECHO    %save%\libs\32-bit\rxtxSerial.dll
ECHO    %save%\libs\64-bit\rxtxSerial.dll
goto over

:closing
IF %javafound%==n (
	ECHO        ERROR: No Java installation was found 
) ELSE (
	ECHO        Java Installation has completed SUCCESSFULLY 
)
IF %javafound%==n (
	goto over
)

goto startDriver

:startDriver
ECHO.
ECHO Installing Arduino UNO Drivers (R2 and R3).........................
%SystemRoot%\System32\InfDefaultInstall.exe %save%\libs\Drivers\Arduino_UNO.inf
IF %sixtybit%==y (
	set progSpot=%save%\libs\64-bit\devcon.exe
) ELSE (
	set progSpot=%save%\libs\32-bit\devcon.exe
)
ECHO.
ECHO Plug an Arduino into the computer.  Windows will show that a driver was not able to be found.  Hit enter once this has happened, then click "Install Anyway"
PAUSE
ECHO.
ECHO Continuing the installation........................................
ECHO.
goto startDriver

:startDriver
%progSpot% update "%save%\libs\Drivers\Arduino_UNO.inf" "USB\VID_2341&PID_0001" >nul 2>nul
IF %errorlevel%==0 (
	set ctrl=0
) ELSE (
	set ctrl=1
)
%progSpot% update "%save%\libs\Drivers\Arduino_UNO.inf" "USB\VID_2341&PID_0043" >nul 2>nul
IF NOT %errorlevel%==0 (
	set /a ctrl=%ctrl%+1
)
if %ctrl%==2 (
	goto errorDriver
) ELSE (
	goto doneDriver
)

:errorDriver
ECHO.
ECHO ERROR: Please plug the Arduino in to your computer before continuing!
PAUSE
goto startDriver

:doneDriver
ECHO.
ECHO         Driver Installation has completed SUCCESSFULLY
ECHO.
ECHO.
ECHO Installation Ended.
CD %save%
PAUSE

:over

