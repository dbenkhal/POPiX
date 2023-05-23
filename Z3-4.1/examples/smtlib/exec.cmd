echo off
set Z3=..\..\bin\z3.exe

for /r %%F in (*.smt2) do (call :TEST "%%F")
goto :EOF

:TEST
      echo %1
      %Z3% %1
      goto :EOF
