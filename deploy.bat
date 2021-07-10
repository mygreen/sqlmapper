@echo off

%~d0
cd %~p0

call env.bat

call mvn -version

rem call mvn clean deploy -pl %MVN_DEPLOY_PROJECT_LIST% -P deploy
call mvn clean deploy -Dmaven.test.skip=true -pl %MVN_DEPLOY_PROJECT_LIST% -P deploy

pause
