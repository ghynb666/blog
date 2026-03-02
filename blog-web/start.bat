@echo off
chcp 65001 >nul
echo Starting Blog Web...
cd /d "%~dp0"
call npm run dev
pause
