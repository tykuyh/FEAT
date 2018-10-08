# FEAT
FEAT is a unified framework to evaluate the Android automated testing techniques.

FEAT allow the users to access their automated testing techniques by implement some interface.
So far, we access three three automated techniques which have good compatibility:
- Monkey
- AppCrawler
- Appium

Given an Android app source code, you can use FEAT will instrument and compile it, then use the techniques have been accessed to test it, for evaluate the techniques.

# Configuration
1. Recommand to use in Java after 1.7
2. Maven is needed
3. The configuration of the techniques you want to use is needed.

### Demands for use 
1. cd to /FEAT/lib and 'sudo chmod 777 aapt' to give permissions for 'aapt' to extract the apk file.

2.When use Appium, you should add the Appium script into FEAT.jar
	1. rename the testing srcipt to ``Main.java``
	2. move it into package ``FEAT.AppiumTestCode``
	3. execute: mvn install:install-file -Dfile=lib/apkUtil-1.0.jar -DgroupId=apkUtil -DartifactId=apkUtil -Dversion=1.0 -Dpackaging=jar
	4. execute: mvn assembly:assembly and use the new jar file

3.If you want to use mutation testing, you should confirm that:
After insturmentation, your app Project can create to an apk success. 
In other word, try to create apk file once.

4. ExecutionConfig.txt need to fill in. 
	1. ``AppDir`` means the relative  path of the main module of these projects. In general, it is "/app"
	2. ``SourceCodeDir`` means the relative path of java code in the main module. In general it is "/app/src/main/java"
	3. ``ClassesDir`` means the relative path of class file of this project. In general it is "/app/build/intermediates/classes"
	4. ``ReportDir`` means the path of report created. In general, it is not need to modify.
	5. ``ApkCreatePath`` means the relative path of the Android application package that created. In general, it is "/app/build/outputs/apk/app-debug.apk"

In the next version, we will automated acquire these information.
# Usage
The runnable jar can be found in: ``FEAT-code-public/FEAT.jar``
To run FEAT, you should use the following command, and specifying the required arguments:
```
Instrumentation:
java -jar FEAT.jar I <AppProjrctDir> <AppPackageName>

Mutation create:
java -jar FEAT.jar MC <AppProjrctDir> <AppPackageName> <OutDir> 

Automated testing techniques:
java -jar FEAT.jar T <AppProjrctDir> <AppPackage> <OutDir> <ToolName> [special parameters]

```
### Arguments
Provide the following list of required arguments when running FEAT: 
1. ``AppProjrctDir``: path of the app project;
2. ``AppPackage`` : path of the apk which used to do the testing;
3. ``AppPackageName``: App main package name;
4. ``OutDir`` : path to output the testing result;
5. ``ToolName`` : Name of the automated testing tools. Only need when use automated tesing tools;
[special parameters]:
1. `` RunScript`` : path of the scripts which control the automated testing tools. Only need when use automated tesing tools;
2. ``ToolDir`` : path of the dir to store automated testing tools. Only need when use automated tesing tools;
The other arguments of FEAT should be complete by editing the ``AppdirConfig.txt`` file and ``PointConfig.txt`` file. The formal is used to set the related information of app structure, the latter is used to set the score rule.
### Example
```
Instrumentation:
java -jar FEAT.jar I /Trial/memetastic io.github.gsantner.memetastic

Mutation create:
java -jar FEAT.jar MC /Trial/memetastic io.github.gsantner.memetastic /outputs/memetastic

Appium:
java -jar FEAT.jar T /Trial/memetastic /Trial/memetastic.apk /outputs/memetastic Appium

Monkey:
java -jar FEAT.jar T /Trial/memetastic /Trial/memetastic.apk /outputs/memetastic Monkey Commands/linux/monkey.sh

AppCrawler:
java -jar FEAT.jar T /Trial/memetastic /Trial/memetastic.apk /outputs/memetastic AppCrawler Commands/linux/appcrawler.sh /automatorTools


```

### Output
The output directory will contain a folder for each techniques in each mobile devices in the output path you send in.


# Future Work
- Only can use in Linux now, we wll add windows compatibility in the future.
- Enhanced exception remove duplicates
- Find new method to detect mutation is killed or not(by coverage ratio now)
