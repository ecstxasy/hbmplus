@echo off
set PROJECT_DIR=%~dp0

cd /d %PROJECT_DIR%

echo adding to git
git add .

set /p COMMIT_MSG="commit message: "
git commit -m "%COMMIT_MSG%"

echo uploading to GitHub...
git push origin master

echo sync completed
pause