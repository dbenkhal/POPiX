set FSHARP_HOME=C:\Program Files (x86)\Microsoft F#\v4.0
set FSC="%FSHARP_HOME%\fsc.exe" 

copy ..\bin\Microsoft.Z3V3.dll .

%FSC% -r:Microsoft.Z3V3.dll --platform:x86 Trail.fs PartialOrderTheory.fs


%FSC% -r:Microsoft.Z3V3.dll -r:FSharp.PowerPack.dll -r:FSharp.PowerPack.Linq.dll Util.fs Quotations.fs --target:library --platform:x86 --out:Microsoft.Z3.FSharpUtils.dll 



