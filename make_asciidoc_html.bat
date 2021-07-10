@echo off

REM AsciiDoc�̃h�L�������g���r���h���Atareget��site�t�H���_�ɔz�u����B

echo "Building AsciiDoc documentation"

%~d0
cd %~p0

pushd .\docs

rem rmdir /q /s _build
call asciidoctor -D _build index.adoc

rem ���\�[�X�̃R�s�[
mkdir .\_build\_static
xcopy /y /s /q .\_static .\_build\_static

popd

rmdir /q /s .\target\site\how2docs
mkdir .\target\site\how2docs
xcopy /y /e .\docs\_build .\target\site\how2docs

REM github-pages��sphinx�Ή�
echo "" > .\target\site\.nojekyll

rem pause
