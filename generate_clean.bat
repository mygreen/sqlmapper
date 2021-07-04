@echo off

%~d0
cd %~p0

call env.bat

call mvn clean


pause
