@echo off
title Compile
cd src
"C:\Program Files (x86)\Java\jdk1.8.0_60\bin\javac.exe" -cp . -d ../bin/ ./Main.java
pause