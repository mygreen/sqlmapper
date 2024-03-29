@echo off

%~d0
cd %~p0

set LOG_FILE="target/javadoc.log"

call env.bat

call mvn clean

mkdir target
call mvn --version > %LOG_FILE% 2>&1 
call mvn -e javadoc:aggregate  -pl %MVN_DEPLOY_PROJECT_LIST% >> %LOG_FILE% 2>&1 

start target/javadoc.log

