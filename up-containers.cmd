@echo off
setlocal ENABLEDELAYEDEXPANSION

REM Change to the directory of this script (repo root)
cd /d "%~dp0"

REM Ensure persistent volume folder exists
if not exist "docker_volume" (
  mkdir "docker_volume"
)

REM Use Docker Compose only
set "COMPOSE_CMD=docker compose"
where docker >nul 2>nul
if not %errorlevel%==0 (
  echo Error: Docker was not found in PATH.
  echo Please install Docker and try again.
  exit /b 1
)

echo Using: %COMPOSE_CMD%
%COMPOSE_CMD% up -d --build
set "EXITCODE=%ERRORLEVEL%"

if not "%EXITCODE%"=="0" (
  echo Compose up failed with exit code %EXITCODE%.
  exit /b %EXITCODE%
)

echo Services are up.
%COMPOSE_CMD% ps
exit /b 0
