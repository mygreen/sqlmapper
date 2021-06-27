@echo off

%~d0
cd %~p0

set LOG_FILE="target/site.log"
rem set MAVEN_OPTS="-Xmx1024m"

call env.bat

call mvn clean

mkdir target
call mvn --version > %LOG_FILE% 2>&1 
call mvn site -Dgpg.skip=true -pl %MVN_PROJECT_LIST% >> %LOG_FILE% 2>&1 

REM github-pagesの対応
echo "" > .\target\site\.nojekyll

start target/site.log

pause
