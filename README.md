# FEAT
FEAT is a unified framework to evaluate the Android automated testing techniques.

FEAT allow the users to access their automated testing techniques by implement some interface.
So far, we access three three automated techniques which have good compatibility:
- Monkey
- AppCrawler
- Appium

Given an Android app source code, you can use FEAT will instrument and compile it, then use the techniques have been accessed to test it, for evaluate the techniques.

# Configuration
1. Recommand to use in Java 1.7
2. Maven is needed
3. The configuration of the techniques you want to use.


# Usage
The runnable jar can be found in: ``FEAT-code-public/FEAT.jar``
To run FEAT, you should use the following command, and specifying the required arguments:
```
Instrumentation:
java -jar FEAT.jar <Operator> <AppProjrctDir> <AppPackage>

Automated testing techniques:
java -jar FEAT.jar <Operator> <AppProjrctDir> <ApkPath> <OutDir> <ToolName> [special parameters]

Mutation create:
java -jar FEAT.jar <Operator> <AppProjrctDir> <AppPackage> <OutDir> 

Mutation testing:
java -jar FEAT.jar <Operator> <AppProjrctDir> <OutDir> <ToolName> [special parameters]

Score:
java -jar FEAT.jar <Operator> <OutDir> <ToolName>


```
If you want to access your automated testing techniques, you can visit [???] for help.

### Arguments
Provide the following list of required arguments when running FEAT: 
1. ``Operator``: The argument to choose run mode. ``I`` for Instrumentation, ``T`` for evaluate automated testing techniques, ``MC`` for mutation creating, and ``ME`` for mutation testing execution.
2. ``AppProjrctDir``: path of the app project;
3. ``ApkPath`` : path of the apk which used to do the testing;
4. ``AppPackage``: App main package name;
5. ``OutDir`` : path to output the testing result;
6. ``ToolName`` : Name of the automated testing tools. Only need when use automated tesing tools;
[special parameters]:
1. `` RunScript`` : path of the scripts which control the automated testing tools. Only need when use automated tesing tools;
2. ``ToolDir`` : path of the dir to store automated testing tools. Only need when use automated tesing tools;
The other arguments of FEAT should be complete by editing the ``AppdirConfig.txt`` file and ``PointConfig.txt`` file. The formal is used to set the related information of app structure, the latter is used to set the score rule.
### Example
```
Instrumentation:
java -jar FEAT.jar I /projects/memetastic io.github.gsantner.memetastic
We recommand to try to compile the app after instrumentation

Appium:
java -jar FEAT.jar T /projects/memetastic /apk/memetastic.apk /outputs/memetastic Appium

Appium need to run a manual automated script, the users should add a script ``Main.java`` in the package ``FEAT.AppiumTestCode`` and use 
``mvn assembly:assembly`` to recreate the FEAT.jar

Monkey:
java -jar FEAT.jar T /projects/memetastic /apk/memetastic.apk /outputs/memetastic Monkey Commands/linux/monkey.sh

AppCrawler:
java -jar FEAT.jar T /projects/memetastic /apk/memetastic.apk /outputs/memetastic AppCrawler Commands/linux/appcrawler.sh /automatorTools

Mutation create:
java -jar FEAT.jar MC /projects/memetastic io.github.gsantner.memetastic /outputs/memetastic

Mutation testing:
java -jar FEAT.jar ME /projects/memetastic /outputs/memetastic [special parameters]

For example, if the users want to evaluate Monkey with mutation testing, they can use:
java -jar FEAT.jar ME /projects/memetastic /outputs/memetastic Monkey Commands/linux/monkey.sh

Score:
java -jar FEAT.jar S /outputs/memetastic Appium


```

### Output
The output directory will contain a folder for each techniques in each mobile devices in the output path you send in.

### Prehaps problem 
Q: Cannot run program "lib/aapt": error=13, Permission denied

A: cd to /FEAT/lib and 'sudo chmod 777 aapt'

before mvn assembly:assembly
mvn install:install-file -Dfile=apkUtil-1.0.jar -DgroupId=apkUtil -DartifactId=apkUtil -Dversion=1.0 -Dpackaging=jar

# Future Work
- Only can use in Linux now, we wll add windows compatibility in the future.
- Enhanced exception remove duplicates
-
