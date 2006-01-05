@echo off
setlocal
set ME_HOME=%~dp0
java -Dmapexplorer.home=%ME_HOME% -jar %ME_HOME%\MapExplorer.jar %*
endlocal
