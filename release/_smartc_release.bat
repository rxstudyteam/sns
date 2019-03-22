@echo off
set JAVA_HOME=D:\window\android-studio34\jre\jre
cd ..
rem call gradlew :app:androidDependencies
call gradlew :app:dependencies > ./release/dependencies.txt
cd release
pause