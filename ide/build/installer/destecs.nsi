; example1.nsi
;
; This script is perhaps one of the simplest NSIs you can make. All of the
; optional settings are left to their default settings. The installer simply 
; prompts the user asking them where to install, and drops a copy of example1.nsi
; there. 

;--------------------------------
!include zipdll.nsh
!include LogicLib.nsh
!include x64.nsh

!include 'FileFunc.nsh'
!insertmacro Locate
 
Var /GLOBAL switch_overwrite

!include 'MoveFileFolder.nsh'
; The name of the installer
Name "DESTECS Installer"

; The file to write
OutFile "destecsInstaller.exe"

; The default installation directory
InstallDir $PROGRAMFILES\DESTECS

; Request application privileges for Windows Vista
RequestExecutionLevel admin

;--------------------------------

; Pages

Page directory
Page instfiles

;--------------------------------

; The stuff to install
Section "" ;No components page, name is not important

  StrCpy $switch_overwrite 0
  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  File data\DestecsIde-1.0.2I-win32.win32.x86.zip
  
  ; DESTECS Tool
  Call DESTECSInstall


  File data\20-sim4.1.3.2.exe
  ; 20-sim
  Call 20simInstall
  
  ;Moving files from DESTECS folder to root of $INSTDIR
  

SectionEnd ; end the section


; Install DESTECS Tool
Function DESTECSInstall
  DetailPrint "Installing DESTECS Tool"
  ; copying to $INSTDIR folder
  
  ; Unzip the file
  ZipDLL::extractall "DestecsIde-1.0.2I-win32.win32.x86.zip" $INSTDIR
  ; Delete the zip
  Delete DestecsIde-1.0.2I-win32.win32.x86.zip
  !insertmacro MoveFolder "$INSTDIR\DestecsIde-1.0.2I\" $INSTDIR "*.*"
  
FunctionEnd


; Install 20-sim function
Function 20simInstall
  DetailPrint "Installing 20-sim"
  
  ;Executing the installer
  ExecWait  '"$INSTDIR\20-sim4.1.3.2.exe"'
  Delete 20-sim4.1.3.2.exe
  Call updateRegistry
  Call copyXRL
FunctionEnd



; Copying DestecsInterface.xrl interface to 20-sim bin
Function copyXRL

File data\DestecsInterface.xrl
${If} ${RunningX64} 
    DetailPrint "Copying DestecsInterface.xrl interface to 20-sim bin (64-bit)"
    IfFileExists "$PROGRAMFILES32\20-sim 4.1\bin" BinDirExists64 PastBinDirExists64
    PastBinDirExists64:
      Abort "Could not copy DestecsInterface.xrl to 20-sim directory location"
    BinDirExists64:
      DetailPrint "$PROGRAMFILES32\20-sim 4.1\bin exists"
      CopyFiles DestecsInterface.xrl  "$PROGRAMFILES32\20-sim 4.1\bin exists"
      Delete DestecsInterface.xrl
${Else}
    DetailPrint "Copying DestecsInterface.xrl interface to 20-sim bin (32-bit)"
    IfFileExists "$PROGRAMFILES\20-sim 4.1\bin" BinDirExists32 PastBinDirExists32
    PastBinDirExists32:
      Abort "Could not copy DestecsInterface.xrl to 20-sim directory location"
    BinDirExists32:
      DetailPrint "$PROGRAMFILES\20-sim 4.1\bin exists"
      CopyFiles DestecsInterface.xrl "$PROGRAMFILES\20-sim 4.1\bin"
      Delete DestecsInterface.xrl
${EndIf}

FunctionEnd 




; Function to update registry
Function updateRegistry
${If} ${RunningX64}
    ; Updating registry for x64
    DetailPrint "Updating Windows registry (64-bit)"
    Call  x64registry
    ;File XMLRPCSupport_x64.reg
    ;ExecWait "REGEDIT.EXE XMLRPCSupport_x64.reg" 
    ;Delete XMLRPCSupport_x64.reg
${Else}
    ; Updating registry for x86
    DetailPrint "Updating Windows registry (32-bit)"
    ;File XMLRPCSupport_x86.reg
    Call x86registry
    ;ExecWait "REGEDIT.EXE XMLRPCSupport_x86.reg" 
    ;Delete XMLRPCSupport_x86.reg
${EndIf}
FunctionEnd



; x64 registry setup
Function x64registry
WriteRegStr HKEY_CLASSES_ROOT "EMX_File" "" "20-sim model"
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shell" "" ""
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shell\open" "" ""
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shell\open\command" "" "$\"$PROGRAMFILES32\20-sim 4.1\bin\20sim.exe$\"  -xmlrpc $\"%1$\""
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shellex" "" ""
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shellex\ContextMenuHandlers" "" "EM_Menu"
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shellex\ContextMenuHandlers\EM_Menu" "" "{DB3247B6-944D-473D-A85A-00CC40BC3954}"
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shellex\IconHandler" "" "{DB3247B6-944D-473D-A85A-00CC40BC3954}"
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shellex\PropertySheetHandlers" "" "EM_Page"
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shellex\PropertySheetHandlers\EM_Page" "" "{DB3247B6-944D-473D-A85A-00CC40BC3954}"
FunctionEnd

; x86 registry setup
Function x86registry

WriteRegStr HKEY_CLASSES_ROOT "EMX_File" "" "20-sim model"
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shell" "" ""
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shell\open" "" ""
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shell\open\command" "" "$\"$PROGRAMFILES\20-sim 4.1\bin\20sim.exe$\"  -xmlrpc $\"%1$\""
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shellex" "" ""
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shellex\ContextMenuHandlers" "" "EM_Menu"
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shellex\ContextMenuHandlers\EM_Menu" "" "{DB3247B6-944D-473D-A85A-00CC40BC3954}"
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shellex\IconHandler" "" "{DB3247B6-944D-473D-A85A-00CC40BC3954}"
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shellex\PropertySheetHandlers" "" "EM_Page"
WriteRegStr HKEY_CLASSES_ROOT "EMX_File\shellex\PropertySheetHandlers\EM_Page" "" "{DB3247B6-944D-473D-A85A-00CC40BC3954}"

FunctionEnd

