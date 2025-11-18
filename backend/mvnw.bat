@echo off
setlocal
set WRAPPER_JAR=%~dp0\.mvn\wrapper\maven-wrapper.jar
if not exist "%WRAPPER_JAR%" (
  echo Maven Wrapper jar not found at %WRAPPER_JAR%
  exit /b 1
)
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain
set MAVEN_HOME=%M2_HOME%
set JAVA_EXE=java
if defined JAVA_HOME set JAVA_EXE="%JAVA_HOME%\bin\java.exe"
"%JAVA_EXE%" -classpath "%WRAPPER_JAR%" %WRAPPER_LAUNCHER% %*
endlocal
