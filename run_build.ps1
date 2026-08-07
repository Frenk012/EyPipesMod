$env:JAVA_HOME = "C:\Users\franc\.jdks\temurin-21"
Set-Location "c:\Users\franc\IdeaProjects\EyPipesMod"
# `build` alone builds every Minecraft version; 1.21.5 is not ported yet.
& .\gradlew.bat :1.21.1:build :1.21.10:build
