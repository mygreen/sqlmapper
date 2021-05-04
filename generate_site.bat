@echo off

%~d0
cd %~p0

set LOG_FILE="target/site.log"

call env.bat

rem call mvn clean

mkdir target
call mvn --version > %LOG_FILE% 2>&1 
call mvn -e package -Dmaven.test.skip=true >> %LOG_FILE% 2>&1 
call mvn -e -N pre-site -Dmaven.test.skip=true >> %LOG_FILE% 2>&1 

start target/site.log

