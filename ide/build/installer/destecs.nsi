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
!define PRODUCT_REG_KEY "DESTECS"
!define PRODUCT_NAME "DESTECS"

!define TARBALL "combined.tar"

!define SIM20_NAME "20-sim"
!define SIM20_VERSION "4.1.3.8"
!define SIM20_EXE "${SIM20_NAME}${SIM20_VERSION}.exe"

!define DESTECSIDE "DestecsIde-"
!define DESTECSFOLDER "${DESTECSIDE}${PRODUCT_VERSION}"
;!define DESTECSZIP "${DESTECSFOLDER}-win32.win32.x86.zip"
!define DESTECSZIP "destecs.zip"

!include "WordFunc.nsh"
  !insertmacro VersionCompare

Var UNINSTALL_OLD_VERSION


; The name of the installer
Name "DESTECS"

; The file to write
OutFile "destecsInstaller_#VERSION#.exe"

; The default installation directory
InstallDir $PROGRAMFILES\DESTECS

; Registry key to check for directory (so if you install again, it will 
; overwrite the old one automatically)
InstallDirRegKey HKLM "Software\DESTECS" "Install_Dir"

; Request application privileges for Windows Vista
RequestExecutionLevel admin
;--------------------------------
;Interface Configuration

  !define MUI_HEADERIMAGE 
  !define MUI_HEADERIMAGE_BITMAP "destecs.bmp" ; optional
  !define MUI_ABORTWARNING
  !define MUI_LICENSEPAGE_TEXT_BOTTOM "This instalation includes DESTECS ${PRODUCT_VERSION} and 20-sim ${SIM20_VERSION}."
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
Section "DESTECS (required)" ;No components page, name is not important

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

  ; Calling the function that installs DESTECS  
  Call DESTECSInstall
  
  ; Calling the function that installs 20-sim
  Call 20simInstall
  
  call 20simVersionTest
  Call writeRegistryKey 
  
  AccessControl::GrantOnFile "$INSTDIR" "(BU)" "GenericRead + GenericWrite"
  ; Registry creation
  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\${PRODUCT_REG_KEY} "Install_Dir" "$INSTDIR"
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_REG_KEY}" "DisplayName" "DESTECS Tool"
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
  CreateShortCut "$SMPROGRAMS\${PRODUCT_REG_KEY}\DESTECS.lnk" "$INSTDIR\destecs.exe" "" "$INSTDIR\destecs.exe" 0
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
  DetailPrint "Deleting $INSTDIR\destecs.exe"
  Delete "$INSTDIR\destecs.exe"
  DetailPrint "Deleting $INSTDIR\destecs.ini"
  Delete "$INSTDIR\destecs.ini"
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

; Install DESTECS Tool
Function DESTECSInstall
  ; Print to detail log 
  DetailPrint "Installing DESTECS Tool"
  ; Unzip the file
   ZipDLL::extractall "${DESTECSZIP}" "$INSTDIR"
  
 ; ZipDLL::extractall "${DESTECSZIP}" "$TEMP\destecs"
 ; ExecWait 'xcopy /S /Y $\"$TEMP\destecs\*.*$\" $\"$INSTDIR$\"'
  ;Moving files from DESTECS folder to root of $INSTDIR
  ;!insertmacro MoveFolder "$INSTDIR\${DESTECSFOLDER}\" $INSTDIR "*.*"
  ;ExecWait 'xcopy /S /Y $\"$INSTDIR\${DESTECSFOLDER}$\" $\"$INSTDIR$\"'
  ; Delete the zip and old folder
  ;RMdir /r "$INSTDIR\${DESTECSFOLDER}"
  DetailPrint "Adding DESTECS firewall exception"
  SimpleFC::AddApplication "DESTECS" "$INSTDIR\destecs.exe" 0 2 "" 1
  Pop $0
  DetailPrint "0=Success/1=Error: $0 "
  
  
  Delete "${DESTECSZIP}"
  
  
FunctionEnd


Function 20simVersionTest

ClearErrors
${If} ${RunningX64}
    ReadRegStr $0 HKLM "Software\Wow6432Node\Controllab Products B.V.\20-sim 4.1\" "Version"       
${Else}
    ReadRegStr $0 HKLM "Software\Controllab Products B.V.\20-sim 4.1\" "Version"
${EndIf}


IfErrors isError
DetailPrint "Installed 20sim is version: $0 / 20sim present in the installer is version: ${SIM20_VERSION}"

${VersionCompare} $0 ${SIM20_VERSION} $1
;    $1=0  Versions are equal
;    $1=1  Version1 is newer
;    $1=2  Version2 is newer
IntCmp $1 2 higher lower 
higher:
   DetailPrint "Installing 20sim version $0 present in the installer" 
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

FunctionEnd

; Install 20-sim function
Function 20simInstall
  ; Print to detail log
  DetailPrint "Installing 20-sim"  
  ;Executing the installer
  ExecWait  '"$INSTDIR\${SIM20_EXE}"'
  
  ; Update the Windows Registry
  ;Call updateRegistry
     
  ; NOT NEEDED ANYMORE - Copy the DestecsInterface.xrl to 20-sim folder
  ;Call copyXRL 
   
FunctionEnd



Function writeRegistryKey
WriteRegDWORD HKCU "Software\20-sim\version 4.1\tools\general" "xmlrpc" 1
FunctionEnd

