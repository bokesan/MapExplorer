@echo off
setlocal
set JVM=java
set ME_HOME=%~dp0
if exist "%ME_HOME%" goto HOME_OK
set ME_HOME=%CD%
if exist "%ME_HOME%" goto HOME_OK
rem ME_HOME not correctly set - try jar file w/o path
%JVM% -jar MapExplorer.jar %*
goto DONE
:HOME_OK
rem get rid of trailing backslash or -D won't work
if %ME_HOME:~-1%==\ set ME_HOME=%ME_HOME:~0,-1%
%JVM% "-Dmapexplorer.home=%ME_HOME%" -jar "%ME_HOME%\MapExplorer.jar" %*
:DONE
endlocal
