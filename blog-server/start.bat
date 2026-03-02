@echo off
chcp 65001 >nul
echo Starting Blog Server...
cd /d "%~dp0"
call mvn clean spring-boot:run
pause
