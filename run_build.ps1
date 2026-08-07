$env:JAVA_HOME = "C:\Users\franc\.jdks\temurin-21"
Set-Location "c:\Users\franc\IdeaProjects\EyPipesMod"
# `build` alone builds every Minecraft version; only 1.21.1 is ported so far.
& .\gradlew.bat :1.21.1:build
