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

echo 集約された jacoco-report のコピー
xcopy /S /Y /E /Q report-aggregate\target\site\jacoco-aggregate target\site\jacoco-aggregate

echo 各モジュールのサイトのコピー
xcopy /S /Y /E /Q /I sqlmapper-parent\target\site target\site\sqlmapper-parent
xcopy /S /Y /E /Q /I sqlmapper-parent\sqlmapper-apt\target\site target\site\sqlmapper-parent\sqlmapper-apt
xcopy /S /Y /E /Q /I sqlmapper-parent\sqlmapper-core\target\site target\site\sqlmapper-parent\sqlmapper-core
xcopy /S /Y /E /Q /I sqlmapper-parent\sqlmapper-metamodel\target\site target\site\sqlmapper-parent\sqlmapper-metamodel

xcopy /S /Y /E /Q /I sqlmapper-parent\sqlmapper-spring-boot\target\site target\site\sqlmapper-parent\sqlmapper-spring-boot
xcopy /S /Y /E /Q /I sqlmapper-parent\sqlmapper-spring-boot\sqlmapper-spring-boot-autoconfigure\target\site target\site\sqlmapper-parent\sqlmapper-spring-boot\sqlmapper-spring-boot-autoconfigure
xcopy /S /Y /E /Q /I sqlmapper-parent\sqlmapper-spring-boot\sqlmapper-spring-boot-starter\target\site target\site\sqlmapper-parent\sqlmapper-spring-boot\sqlmapper-spring-boot-starter

REM github-pagesの対応
echo "" > .\target\site\.nojekyll

start target/site.log

pause
