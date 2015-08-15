cd resourcesbuild
FOR %%f IN (*.svg) DO convert "%%f"  "%%~nf".png