@echo off

if NOT "%JAVA_HOME_11%" == "" (
    set JAVA_HOME="%JAVA_HOME_11%"
)

set PATH=%PATH%;%JAVA_HOME%\bin;%M2_HOME%\bin;

rem 処理対象のMavenのプロジェクト(exampleプロジェクトは除外する)
set MVN_PROJECT_LIST="!sqlmapper-sample,!sqlmapper-sample/sqlmapper-sample-spring-boot"

