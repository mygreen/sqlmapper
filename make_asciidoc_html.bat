@echo off

REM AsciiDoc�̃h�L�������g���r���h���Atareget��site�t�H���_�ɔz�u����B

echo "Building AsciiDoc documentation"

%~d0
cd %~p0

pushd .\docs

rem rmdir /q /s _build
call asciidoctor -D _build index.adoc

popd

rmdir /q /s .\target\site\docs
mkdir .\target\site\docs
xcopy /y /e .\docs\_build .\target\site\docs

REM github-pages��sphinx�Ή�
echo "" > .\target\site\.nojekyll
