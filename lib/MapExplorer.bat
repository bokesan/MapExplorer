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
%JVM% "-Dmapexplorer.home=%ME_HOME%" -jar "%ME_HOME%\MapExplorer.jar" %*
:DONE
endlocal
