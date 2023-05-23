This directory contains scripts to build the test 
"fixedpoints plugin" application using
Microsoft C compiler, or gcc.

The sample uses the BuDDy package that you can download from

   http://sourceforge.net/projects/buddy/

You should put header files and buddy.lib under a 
subdirectory called "buddy".

1) Using Microsoft C compiler

Use 'build.cmd' to build the test application using Microsoft C
compiler.

Remark: The Microsoft C compiler (cl) must be in your path,
or you can use the Visual Studio Command Prompt.

The script 'exec.cmd' adds the bin directory to the path. So, 
test_fixedpoints.exe can find z3.dll.


2) Using gcc

Use 'build.sh' to build the test application using gcc. 
The script 'exec.sh' adds the bin directory to the path. So, 
test_fixedpoints.exe can find z3.dll.

Remark: the scripts 'build.sh' and 'exec.sh' assumes you are in a
Cygwin or Mingw shell.

