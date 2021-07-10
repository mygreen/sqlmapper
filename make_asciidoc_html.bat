@echo off

REM AsciiDocのドキュメントをビルドし、taregetのsiteフォルダに配置する。

echo "Building AsciiDoc documentation"

%~d0
cd %~p0

pushd .\docs

rem rmdir /q /s _build
call asciidoctor -D _build index.adoc

rem リソースのコピー
mkdir .\_build\_static
xcopy /y /s /q .\_static .\_build\_static

popd

rmdir /q /s .\target\site\how2docs
mkdir .\target\site\how2docs
xcopy /y /e .\docs\_build .\target\site\how2docs

REM github-pagesのsphinx対応
echo "" > .\target\site\.nojekyll

rem pause
