@ECHO OFF
set javafound=n
set save=%cd%
ECHO ^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=
ECHO ^|   RXTXRobot Installer for Windows   ^|
ECHO ^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=^=

ECHO Installing Java Libraries......................................
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

goto end

:end
ECHO.
ECHO Installing Arduino UNO Drivers (R2 and R3).........................
%SystemRoot%\System32\InfDefaultInstall.exe %save%\libs\Drivers\Arduino_UNO.inf
ECHO         Driver Installation has completed SUCCESSFULLY
ECHO.
ECHO.
ECHO Installation Ended.
CD %save%
PAUSE

:over

