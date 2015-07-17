; No need to compress anything
SetCompress off

;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"
;--------------------------------
!include zipdll.nsh
!include LogicLib.nsh
!include x64.nsh

!include 'FileFunc.nsh'
!insertmacro Locate

!include "WordFunc.nsh"
!insertmacro VersionCompare

Var UNINSTALL_OLD_VERSION


; The name of the installer
Name ${installer.name}

OutFile "${installer.output}"

; The default installation directory
InstallDir $PROGRAMFILES\${installer.name}

; Registry key to check for directory (so if you install again, it will
; overwrite the old one automatically)
InstallDirRegKey HKLM "Software\Crescendo" "Install_Dir"

; Request application privileges for Windows Vista
RequestExecutionLevel admin
;--------------------------------
;Interface Configuration

  !define MUI_HEADERIMAGE
  !define MUI_HEADERIMAGE_BITMAP "${installer.path}\Crescendo.bmp" ; optional
  !define MUI_ICON "${installer.path}\crescendo_installer.ico"
  !define MUI_UNICON "${installer.path}\crescendo_uninstaller.ico"
  !define MUI_ABORTWARNING
  !define MUI_LICENSEPAGE_TEXT_BOTTOM "This instalation includes Crescendo ${crescendo.version} and ${sim20.name} ${sim20.version}."
;--------------------------------


;--------------------------------

; Pages
;Page components
;Page directory
;Page instfiles
;!insertmacro MUI_PAGE_WELCOME

!insertmacro MUI_PAGE_LICENSE "${installer.path}\license.txt"
!insertmacro MUI_PAGE_COMPONENTS

;Var StartMenuFolder
;!insertmacro MUI_PAGE_STARTMENU "Application" $StartMenuFolder

!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------

!insertmacro MUI_LANGUAGE "English"

; The stuff to install
Section "Crescendo (required)" ;No components page, name is not important

  SectionIn RO

  ; Set output path to the installation directory.
  SetOutPath $INSTDIR

  ;
  StrCmp $UNINSTALL_OLD_VERSION "" core.files
  ExecWait '$UNINSTALL_OLD_VERSION'

  core.files:
  WriteRegStr HKLM "Software\${installer.regkey}" "" $INSTDIR
  WriteRegStr HKLM "Software\${installer.regkey}" "Version" "${crescendo.version}"

  ;; Combined Tools instalation file
  File "${sim20.path}\${sim20.exe}"
  File "${crescendo.path}\${crescendo.zip}"

  ; Calling the function that installs Crescendo
  Call DESTECSInstall

  ; Calling the function that installs 20-sim
  call 20simVersionTest

  AccessControl::GrantOnFile "$INSTDIR" "(BU)" "GenericRead + GenericWrite"
  ; Registry creation
  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\${installer.regkey} "Install_Dir" "$INSTDIR"
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${installer.regkey}" "DisplayName" "${crescendo.name}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${installer.regkey}" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${installer.regkey}" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${installer.regkey}" "NoRepair" 1
  WriteUninstaller "uninstall.exe"

SectionEnd ; end the section

; Optional section (can be disabled by the user)
Section "Start Menu Shortcuts (Optional)"
  SetShellVarContext all
  CreateDirectory "$SMPROGRAMS\${installer.regkey}"
  CreateShortCut "$SMPROGRAMS\${installer.regkey}\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\${installer.regkey}\Crescendo.lnk" "$INSTDIR\Crescendo.exe" "" "$INSTDIR\Crescendo.exe" 0
SectionEnd

; Uninstaller
Section "Uninstall"

  ;deleting the uninstall exe first is apparently normal
  Delete $INSTDIR\uninstall.exe


  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${installer.regkey}"
  DeleteRegKey HKLM SOFTWARE\${installer.regkey}
  ; Remove files and uninstaller
  ;Delete $INSTDIR\example2.nsi
  DetailPrint "Deleting $INSTDIR\configuration"
  RMDir /r "$INSTDIR\configuration"
  DetailPrint "Deleting $INSTDIR\features"
  RMDir /r "$INSTDIR\features"
  DetailPrint "Deleting $INSTDIR\p2"
  RMDir /r "$INSTDIR\p2"
  DetailPrint "Deleting $INSTDIR\plugins"
  RMDir /r "$INSTDIR\plugins"
  DetailPrint "Deleting $INSTDIR\readme"
  RMDir /r "$INSTDIR\readme"
  DetailPrint "Deleting $INSTDIR\.eclipseproduct"
  Delete "$INSTDIR\.eclipseproduct"
  DetailPrint "Deleting $INSTDIR\artifacts.xml"
  Delete "$INSTDIR\artifacts.xml"
  DetailPrint "Deleting $INSTDIR\Crescendo.exe"
  Delete "$INSTDIR\Crescendo.exe"
  DetailPrint "Deleting $INSTDIR\Crescendo.ini"
  Delete "$INSTDIR\Crescendo.ini"
  DetailPrint "Deleting $INSTDIR\epl-v10.html"
  Delete "$INSTDIR\epl-v10.html"
  DetailPrint "Deleting $INSTDIR\notice.html"
  Delete "$INSTDIR\notice.html"
  DetailPrint "Deleting $INSTDIR\eclipsec.exe"
  Delete "$INSTDIR\eclipsec.exe"


 SetShellVarContext all
  ; Remove shortcuts, if any
  Delete "$SMPROGRAMS\${installer.regkey}\*.*"
  ; Remove directories used
  RMDir "$SMPROGRAMS\${installer.regkey}"


  RMDir "$INSTDIR"
SectionEnd


Function .onInit
  ;Check earlier installation
  ClearErrors
  ReadRegStr $0 HKLM "Software\${installer.regkey}" "Version"
  IfErrors init.uninst ; older versions might not have "Version" string set
  ${VersionCompare} $0 ${crescendo.version} $1
  IntCmp $1 2 init.uninst
    MessageBox MB_YESNO|MB_ICONQUESTION "${installer.name} version $0 seems to be already installed on your system.$\nWould you like to proceed with the installation of version ${crescendo.version}?" \
        IDYES init.uninst
    Quit

init.uninst:
  ClearErrors
  ReadRegStr $0 HKLM "Software\${installer.regkey}" ""
  IfErrors init.done
  StrCpy $UNINSTALL_OLD_VERSION '"$0\uninstall.exe" /S _?=$0'

init.done:
FunctionEnd

; Install Crescendo Tool
Function DESTECSInstall
  ; Print to detail log
  DetailPrint "Installing ${crescendo.name}"
  ; Unzip the file
  ZipDLL::extractall "${crescendo.zip}" "$INSTDIR"

  ; ZipDLL::extractall "${crescendo.zip}" "$TEMP\destecs"
  ;ExecWait 'xcopy /S /Y $\"$TEMP\destecs\*.*$\" $\"$INSTDIR$\"'
  ; Moving files from DESTECS folder to root of $INSTDIR
  ;!insertmacro MoveFolder "$INSTDIR\${crescendo.installfolder}\" $INSTDIR "*.*"
  ;ExecWait 'xcopy /S /Y $\"$INSTDIR\${crescendo.installfolder}$\" $\"$INSTDIR$\"'
  ; Delete the zip and old folder
  ;RMdir /r "$INSTDIR\${crescendo.installfolder}"
  DetailPrint "Adding Crescendo firewall exception"
  SimpleFC::AddApplication "Crescendo" "$INSTDIR\Crescendo.exe" 0 2 "" 1
  Pop $0
  DetailPrint "0=Success/1=Error: $0 "

  Delete "${crescendo.zip}"
FunctionEnd


Function 20simVersionTest

ClearErrors
${If} ${RunningX64}
    ReadRegStr $0 HKLM "Software\Wow6432Node\Microsoft\Windows\CurrentVersion\Uninstall\${sim20.regkey}\" "DisplayVersion"
${Else}
    ReadRegStr $0 HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${sim20.regkey}\" "DisplayVersion"
${EndIf}


IfErrors isError
DetailPrint "Installed ${sim20.name} is version: $0 / ${sim20.name} present in the installer is version: ${sim20.version}"

${VersionCompare} $0 ${sim20.version} $1
;    $1=0  Versions are equal
;    $1=1  Version1 is newer
;    $1=2  Version2 is newer
IntCmp $1 2 higher lower
higher:
   DetailPrint "Installing ${sim20.name} version ${sim20.version} present in the installer"
   call 20simInstall
   Goto done
lower:
    DetailPrint "${sim20.name} up to date"
    Goto done
isError:
   DetailPrint "No previous ${sim20.name} version found"
   call 20simInstall
   Goto done
done:
  Delete "${sim20.exe}"

call Add20simFirewallException

FunctionEnd


; adding 20sim firewall exception
Function Add20simFirewallException
ClearErrors
${If} ${RunningX64}
    ReadRegStr $0 HKLM "Software\Wow6432Node\Microsoft\Windows\CurrentVersion\Uninstall\${sim20.regkey}\" "DisplayIcon"
${Else}
    ReadRegStr $0 HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${sim20.regkey}\" "DisplayIcon"
${EndIf}

IfErrors isError
    DetailPrint "Adding ${sim20.name} firewall exception"
    SimpleFC::AddApplication "20sim" "$0" 0 2 "" 1
    goto done
isError:
    DetailPrint "Could not add ${sim20.name} firewall exception"
done:

FunctionEnd


; Install 20-sim function
Function 20simInstall
  ; Print to detail log
  DetailPrint "Installing ${sim20.name}"
  ;Executing the installer
  ExecWait  '"$INSTDIR\${sim20.exe}'
  DetailPrint "Done installing ${sim20.name}"

  ; Update the Windows Registry
  ;Call updateRegistry

  ; NOT NEEDED ANYMORE - Copy the DestecsInterface.xrl to ${sim20.name} folder
  ;Call copyXRL
FunctionEnd

