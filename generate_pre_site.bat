@echo off

%~d0
cd %~p0

set LOG_FILE="target/pre_site.log"

call env.bat

call mvn clean

mkdir target
call mvn --version > %LOG_FILE% 2>&1 
call mvn -e package -Dgpg.skip=true -Dmaven.test.skip=true >> %LOG_FILE% 2>&1 
call mvn -e -N pre-site -Dgpg.skip=true -Dmaven.test.skip=true >> %LOG_FILE% 2>&1 

start target/pre_site.log

