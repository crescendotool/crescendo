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
 

!define PRODUCT_VERSION "0.0.0"
!define PRODUCT_REG_KEY "Crescendo"
!define PRODUCT_NAME "Crescendo"

!define TARBALL "combined.tar"

!define SIM20_NAME "20-sim"
!define SIM20_VERSION "4.3.0"
!define SIM20_PLATFORM "win32"
!define SIM20_EXE "${SIM20_NAME}-${SIM20_VERSION}-${SIM20_PLATFORM}.exe"

!define DESTECSIDE "CrescendoIde-"
!define DESTECSFOLDER "${DESTECSIDE}${PRODUCT_VERSION}"
;!define DESTECSZIP "${DESTECSFOLDER}-win32.win32.x86.zip"
!define DESTECSZIP "Crescendo.zip"

!include "WordFunc.nsh"
  !insertmacro VersionCompare

Var UNINSTALL_OLD_VERSION


; The name of the installer
Name "Crescendo"

OutFile "Crescendo_#VERSION#_#PLATFORM#.exe"

; The default installation directory
InstallDir $PROGRAMFILES\Crescendo

; Registry key to check for directory (so if you install again, it will 
; overwrite the old one automatically)
InstallDirRegKey HKLM "Software\Crescendo" "Install_Dir"

; Request application privileges for Windows Vista
RequestExecutionLevel admin
;--------------------------------
;Interface Configuration

  !define MUI_HEADERIMAGE 
  !define MUI_HEADERIMAGE_BITMAP "Crescendo.bmp" ; optional
  !define MUI_ICON "crescendo.ico"
  ;!define MUI_UNICON "crescendo-uninst.ico"
  !define MUI_ABORTWARNING
  !define MUI_LICENSEPAGE_TEXT_BOTTOM "This instalation includes Crescendo ${PRODUCT_VERSION} and 20-sim ${SIM20_VERSION}."
;-------------------------------- 

;!define MUI_WELCOMEPAGE_TITLE "dsasda"
;!define MUI_WELCOMEPAGE_TITLE_3LINES
;!define MUI_WELCOMEPAGE_TEXT "dsadas sdadas dassd ada dsa dsadsa"


;--------------------------------

; Pages
;Page components
;Page directory
;Page instfiles
  ;!insertmacro MUI_PAGE_WELCOME   
  !insertmacro MUI_PAGE_LICENSE "license.txt"
  !insertmacro MUI_PAGE_COMPONENTS
   ; Var StartMenuFolder
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
  WriteRegStr HKLM "Software\${PRODUCT_REG_KEY}" "" $INSTDIR
  WriteRegStr HKLM "Software\${PRODUCT_REG_KEY}" "Version" "${PRODUCT_VERSION}"

  ; Combined Tools instalation file
  File "data\${TARBALL}"
  ; Call the function to unpack the tarball before installing the tools
  untgz::extract -znone -j -d "$INSTDIR" "${TARBALL}"
  Delete "${TARBALL}"

  ; Calling the function that installs Crescendo  
  Call DESTECSInstall
  
  ; Calling the function that installs 20-sim
  call 20simVersionTest
  Call writeRegistryKey 
  
  AccessControl::GrantOnFile "$INSTDIR" "(BU)" "GenericRead + GenericWrite"
  ; Registry creation
  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\${PRODUCT_REG_KEY} "Install_Dir" "$INSTDIR"
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_REG_KEY}" "DisplayName" "Crescendo Tool"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_REG_KEY}" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_REG_KEY}" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_REG_KEY}" "NoRepair" 1
  WriteUninstaller "uninstall.exe"
  
SectionEnd ; end the section

; Optional section (can be disabled by the user)
Section "Start Menu Shortcuts (Optional)"
  SetShellVarContext all
  CreateDirectory "$SMPROGRAMS\${PRODUCT_REG_KEY}"
  CreateShortCut "$SMPROGRAMS\${PRODUCT_REG_KEY}\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\${PRODUCT_REG_KEY}\Crescendo.lnk" "$INSTDIR\Crescendo.exe" "" "$INSTDIR\Crescendo.exe" 0
SectionEnd

; Uninstaller
Section "Uninstall"

  ;deleting the uninstall exe first is apparently normal
  Delete $INSTDIR\uninstall.exe 
  
  
  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_REG_KEY}"
  DeleteRegKey HKLM SOFTWARE\${PRODUCT_REG_KEY}
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
 
 
 
  ; Remove shortcuts, if any
  Delete "$SMPROGRAMS\${PRODUCT_REG_KEY}\*.*"
  ; Remove directories used
  RMDir "$SMPROGRAMS\${PRODUCT_REG_KEY}"
  
  
  RMDir "$INSTDIR"
SectionEnd


Function .onInit
  ;Check earlier installation
  ClearErrors
  ReadRegStr $0 HKLM "Software\${PRODUCT_REG_KEY}" "Version"
  IfErrors init.uninst ; older versions might not have "Version" string set
  ${VersionCompare} $0 ${PRODUCT_VERSION} $1
  IntCmp $1 2 init.uninst
    MessageBox MB_YESNO|MB_ICONQUESTION "${PRODUCT_NAME} version $0 seems to be already installed on your system.$\nWould you like to proceed with the installation of version ${PRODUCT_VERSION}?" \
        IDYES init.uninst
    Quit

init.uninst:
  ClearErrors
  ReadRegStr $0 HKLM "Software\${PRODUCT_REG_KEY}" ""
  IfErrors init.done
  StrCpy $UNINSTALL_OLD_VERSION '"$0\uninstall.exe" /S _?=$0'

init.done:
FunctionEnd

; Install Crescendo Tool
Function DESTECSInstall
  ; Print to detail log 
  DetailPrint "Installing Crescendo Tool"
  ; Unzip the file
   ZipDLL::extractall "${DESTECSZIP}" "$INSTDIR"
  
 ; ZipDLL::extractall "${DESTECSZIP}" "$TEMP\destecs"
 ; ExecWait 'xcopy /S /Y $\"$TEMP\destecs\*.*$\" $\"$INSTDIR$\"'
  ;Moving files from DESTECS folder to root of $INSTDIR
  ;!insertmacro MoveFolder "$INSTDIR\${DESTECSFOLDER}\" $INSTDIR "*.*"
  ;ExecWait 'xcopy /S /Y $\"$INSTDIR\${DESTECSFOLDER}$\" $\"$INSTDIR$\"'
  ; Delete the zip and old folder
  ;RMdir /r "$INSTDIR\${DESTECSFOLDER}"
  DetailPrint "Adding Crescendo firewall exception"
  SimpleFC::AddApplication "Crescendo" "$INSTDIR\Crescendo.exe" 0 2 "" 1
  Pop $0
  DetailPrint "0=Success/1=Error: $0 "
  
  
  Delete "${DESTECSZIP}"
  
  
FunctionEnd


Function 20simVersionTest

ClearErrors
${If} ${RunningX64}
    ReadRegStr $0 HKLM "Software\Wow6432Node\Microsoft\Windows\CurrentVersion\Uninstall\20-sim 4.3\" "DisplayVersion"       
${Else}
    ReadRegStr $0 HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\20-sim 4.3\" "DisplayVersion"
${EndIf}


IfErrors isError
DetailPrint "Installed 20sim is version: $0 / 20sim present in the installer is version: ${SIM20_VERSION}"

${VersionCompare} $0 ${SIM20_VERSION} $1
;    $1=0  Versions are equal
;    $1=1  Version1 is newer
;    $1=2  Version2 is newer
IntCmp $1 2 higher lower 
higher:
   DetailPrint "Installing 20sim version ${SIM20_VERSION} present in the installer" 
   call 20simInstall   
   Goto done 
lower:
    DetailPrint "20sim up to date"
    Goto done
isError: 
   DetailPrint "No previous 20sim version found"  
   call 20simInstall
   Goto done
done:
  Delete "${SIM20_EXE}"

call Add20simFirewallException

FunctionEnd


; adding 20sim firewall exception
Function Add20simFirewallException
ClearErrors
${If} ${RunningX64}
    ReadRegStr $0 HKLM "Software\Wow6432Node\Microsoft\Windows\CurrentVersion\Uninstall\20-sim 4.3\" "DisplayIcon"       
${Else}
    ReadRegStr $0 HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\20-sim 4.3\" "DisplayIcon"
${EndIf}
 
IfErrors isError 
    DetailPrint "Adding 20sim firewall exception"  
    SimpleFC::AddApplication "20sim" "$0" 0 2 "" 1
    goto done
isError:
    DetailPrint "Could not add 20sim firewall exception"
done:

FunctionEnd


; Install 20-sim function
Function 20simInstall
  ; Print to detail log
  DetailPrint "Installing 20-sim"  
  ;Executing the installer
  ExecWait  '"$INSTDIR\${SIM20_EXE}'
  DetailPrint "Done installing 20-sim"  
  
  ; Update the Windows Registry
  ;Call updateRegistry
     
  ; NOT NEEDED ANYMORE - Copy the DestecsInterface.xrl to 20-sim folder
  ;Call copyXRL 
   
FunctionEnd



Function writeRegistryKey
;${If} ${RunningX64}
;    WriteRegDWORD HKCU "Software\Wow6432Node\20-sim\version 4.3\tools\general" "xmlrpc" 1
;${Else}
    WriteRegDWORD HKCU "Software\20-sim\version 4.3\tools\general" "xmlrpc" 1
;${EndIf}
FunctionEnd

