@echo off
setlocal
set JAVA_HOME=C:\Users\DELL\.jdk\jdk-25.0.2
set MAVEN_HOME=C:\Users\DELL\.maven\maven-3.9.15
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%
cd /d "%~dp0"

mvn -q -DskipTests dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target\dependency
start "" "%JAVA_HOME%\bin\javaw.exe" -cp "target\classes;target\dependency\*" com.cooperativa.desktop.Main

echo Aplicacion iniciada.
endlocal
