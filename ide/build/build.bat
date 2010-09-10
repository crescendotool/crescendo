@echo off
echo Building overture

set BUILD_DIR=%CD%\build



set ECLIPSE_HOME="C:\Users\kela\Downloads\eclipse36"
set BASE=%ECLIPSE_HOME%
set BUILD_XML_PATH="C:\destecs\destecs_sf_svn\trunk\ide\build\"
set OVERTURE_SOURCE_IDE_BASE="C:\overture\overturesvn\ide"
set equinoxLauncherPluginVersion="1.1.0.v20100507"
set pdeBuildPluginVersion="3.6.0.v20100603"
set BASEOS=win32
set BASEWS=win32
set BASEARCH=x86


echo Changing to build.xml directory

cd %BUILD_XML_PATH%

echo Starting Eclipse ANT runner

set LAUNCHER_PLUGIN=%ECLIPSE_HOME%\plugins\org.eclipse.equinox.launcher_%equinoxLauncherPluginVersion%.jar

java -cp %LAUNCHER_PLUGIN% org.eclipse.equinox.launcher.Main -application org.eclipse.ant.core.antRunner -buildfile build.xml -Dbase=%BASE% -DeclipseLocation=%ECLIPSE_HOME% -DequinoxLauncherPluginVersion=%equinoxLauncherPluginVersion% -DbuildDirectory=%BUILD_DIR% -DpdeBuildPluginVersion=%pdeBuildPluginVersion% -Dbaseos=%BASEOS% -Dbasews=%BASEWS% -Dbasearch=%BASEARCH% -DOvertureSourceIdeBase=%OVERTURE_SOURCE_IDE_BASE%

cd %BUILD_DIR%
cd ..

echo Done
pause

