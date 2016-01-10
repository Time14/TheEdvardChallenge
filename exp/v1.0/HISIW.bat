@echo off
color 0a
title "HISIW - v1.0"

java -cp HISIW_lib/lwjgl.jar;HISIW_lib/SKNet.jar;HISIW_lib/TimeAPI.jar;HISIW.jar -Djava.library.path=native xsked.Main

pause