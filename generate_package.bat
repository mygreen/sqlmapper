@echo off

%~d0
cd %~p0

set LOG_FILE="target/package.log"

call env.bat

mkdir target
call mvn --version > %LOG_FILE% 2>&1 
call mvn -e package -Dmaven.test.skip=true >> %LOG_FILE% 2>&1 

start target/package.log

