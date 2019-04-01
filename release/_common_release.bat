set JAVA_HOME=D:\window\android-studio34\jre\jre

del ..\base\libs\common.jar

cd ..
call gradlew :common:createFullJarRelease
cd release

copy ..\common\build\intermediates\intermediate-jars\release\classes.jar ..\base\libs\common.jar
pause