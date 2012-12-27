@ECHO OFF
set javafound=n
set save=%cd%
set sixtybit=y
set win8=n
ECHO ^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=
ECHO ^|   RXTXRobot Installer for Windows   ^|
ECHO ^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=

ECHO.
ECHO Starting Installation!
ECHO.
ECHO Determining OS Version.............................................
FOR /F "delims=: tokens=2" %%i IN ('systeminfo 2^>nul ^| find "OS Name"') DO set vers=%%i
ECHO %vers% | find "Windows 8" >nul
IF %ERRORLEVEL% == 0 set win8=y
IF %win8%%==y (
	ECHO You are running Windows 8!
) ELSE (
	ECHO You are running Windows 7/XP!
)
ECHO.
IF %win8%==y (
	ECHO Attempting to change the driver permissions to work for Windows 8...
	bcdedit -set loadoptions DISABLE_INTEGRITY_CHECKS
	IF %ERRORLEVEL%!=0 (
		ECHO FATAL ERROR: Could not change driver permissions.
		ECHO You must right click the install script and "Run as Administrator"
		goto over
	)
	bcdedit -set TESTSIGNING ON
	IF %ERRORLEVEL% != 0 (
		ECHO FATAL ERROR: Could not change driver permissions.
		ECHO You must right click the install script and "Run as Administrator"
		goto over
	)
)
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

goto beginDriver

:beginDriver
ECHO.
ECHO Installing Arduino UNO Drivers (R2 and R3).........................
IF %sixtybit%==y (
	set progSpot=%save%\libs\64-bit\devcon.exe
) ELSE (
	set progSpot=%save%\libs\32-bit\devcon.exe
)
ECHO.
ECHO Plug an Arduino into the computer.  Windows will show that a driver was not able to be found.
ECHO Hit enter once this has happened, then click "Install this driver software anyway"
ECHO.
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

