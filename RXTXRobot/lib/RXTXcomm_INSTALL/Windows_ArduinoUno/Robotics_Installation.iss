; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "Freshman Robotics Design"
#define MyAppVersion "1.0"
#define MyAppPublisher "Southern Methodist University"
#define MyAppURL "http://lyle.smu.edu/fyd"
[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{AC6BA53E-65E2-494F-8F7E-657B9558EE23}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppName}
DefaultGroupName={#MyAppName}
AllowNoIcons=yes
OutputBaseFilename=setup
Compression=lzma
SolidCompression=yes
ArchitecturesInstallIn64BitMode=x64
RestartIfNeededByRun=no

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "libs\32-bit\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs; Check: not Is64BitInstallMode
Source: "libs\64-bit\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs; Check: Is64BitInstallMode;
Source: "libs\Drivers\Arduino_UNO.inf"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"

[Run]
Filename: "{app}\devcon.exe"; Parameters: "update ""{app}\Arduino_UNO.inf"" ""USB\VID_2341&PID_0001"""; StatusMsg: "Installing Arduino Drivers..."; Flags: runhidden
Filename: "{app}\devcon.exe"; Parameters: "update ""{app}\Arduino_UNO.inf"" ""USB\VID_2341&PID_0043"""; StatusMsg: "Installing Arduino Drivers..."; Flags: runhidden
Filename: "{app}\install_libs.bat"; Parameters: ""; StatusMsg: "Installing Java Libraries..."; Flags: runhidden
Filename: "{app}\Phidget.exe"; Parameters: ""; StatusMsg: "Installing Phidget Libraries...";

[UninstallRun]
Filename: "{app}\devcon.exe"; Parameters: "remove ""USB\VID_2341&PID_0001"""; StatusMsg: "Removing Arduino Drivers..."; Flags: runhidden
Filename: "{app}\devcon.exe"; Parameters: "remove ""USB\VID_2341&PID_0043"""; StatusMsg: "Removing Arduino Drivers..."; Flags: runhidden
Filename: "{app}\uninstall_libs.bat"; Parameters: ""; StatusMsg: "Uninstalling Java Libraries..."; Flags: runhidden
Filename: "{app}\Phidget.exe"; Parameters: ""; StatusMsg: "Uninstalling Phidget Libraries...";

[Code]
function NextButtonClick(CurPageID: Integer): Boolean;
var
  TmpFileName1, TmpFileName2, ExecStdout1, ExecStdout2: string;
  ResultCode1: Integer;
  ResultCode2: Integer;
  noContinue: Boolean;
  JavaOutput: Integer;
begin
  if CurPageID = wpReady then begin
    JavaOutput := MsgBox('Java JDK MUST be installed before continuing.  Please make sure it is installed before continuing, otherwise the installer will not function correctly!', mbInformation, MB_OKCANCEL);
    if JavaOutput = IDCANCEL then begin
      Result := false;
    end else begin;
      TmpFileName1 := ExpandConstant('{tmp}') + '\result1.txt';
      TmpFileName2 := ExpandConstant('{tmp}') + '\result2.txt';
      noContinue := false;
      ExtractTemporaryFile('devcon.exe');
      Exec(ExpandConstant('{cmd}'), '/C "' + ExpandConstant('{tmp}') + '\devcon.exe find USB\VID_2341^&PID_0001 > ' + TmpFileName1, '', SW_HIDE, ewWaitUntilTerminated, ResultCode1);
      Exec(ExpandConstant('{cmd}'), '/C "' + ExpandConstant('{tmp}') + '\devcon.exe find USB\VID_2341^&PID_0043 > ' + TmpFileName2, '', SW_HIDE, ewWaitUntilTerminated, ResultCode2);
      LoadStringFromFile(TmpFileName1, ExecStdout1);
      LoadStringFromFile(TmpFileName2, ExecStdout2);
      ExecStdout1 := Copy(ExecStdout1, 0, 3);
      ExecStdout2 := Copy(ExecStdout2, 0, 3);
      while (ExecStdout1 = 'No ') AND (ExecStdout2 = 'No ') AND (noContinue = false)  do
      begin
        if MsgBox('No Arduino was detected.  Plug one into the computer and retry.', mbInformation, MB_RETRYCANCEL) = IDRETRY then begin
          Exec(ExpandConstant('{cmd}'), '/C "' + ExpandConstant('{tmp}') + '\devcon.exe find USB\VID_2341^&PID_0001 > ' + TmpFileName1, '', SW_HIDE, ewWaitUntilTerminated, ResultCode1);
          Exec(ExpandConstant('{cmd}'), '/C "' + ExpandConstant('{tmp}') + '\devcon.exe find USB\VID_2341^&PID_0043 > ' + TmpFileName2, '', SW_HIDE, ewWaitUntilTerminated, ResultCode2);
          LoadStringFromFile(TmpFileName1, ExecStdout1);
          LoadStringFromFile(TmpFileName2, ExecStdout2);
          ExecStdout1 := Copy(ExecStdout1, 0, 3);
          ExecStdout2 := Copy(ExecStdout2, 0, 3);
        end else begin
          noContinue := true;
        end;
      end;
      if noContinue = true then begin
        Result := false;
      end else begin
        Result := true;
      end;
    end;
  end else begin
    Result := true;
  end;
end;

procedure CancelButtonClick(CurPageID: Integer; var Cancel, Confirm: Boolean);
begin
  Cancel := true;
  Confirm := true;
end;



