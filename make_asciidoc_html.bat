@echo off

REM AsciiDoc�̃h�L�������g���r���h���Atareget��site�t�H���_�ɔz�u����B

echo "Building AsciiDoc documentation"

%~d0
cd %~p0

pushd .\src\site\asciidoc

rem rmdir /q /s _build
call asciidoctor -D _build index.adoc

popd

rmdir /q /s .\target\site\asciidoc
mkdir .\target\site\asciidoc
xcopy /y /e .\src\site\asciidoc\_build .\target\site\asciidoc

REM github-pages��sphinx�Ή�
echo "" > .\target\site\.nojekyll
