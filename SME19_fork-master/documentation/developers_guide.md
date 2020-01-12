# Developers guide

## Import project in Eclipse
Select File>Import>Projects from Git and set the following path and your user credentials

https://github.com/GLopezMUZH/SME19_TestDescriberProject.git


1. Import the projects: "Task1" as java projects 
2. Import the Project "TestDescriber2" as Maven project
3. Click on the project "TestDescriber-summarizer" -> "Build Path-> Configure Build Path", then to add to the project the jar library "Classifier4J-0.6.jar" (located under "/TestDescriber2/TestDescriber-summarizer/lib/")
4. Click on the project "TestDescriber-summarizer" -> "Build Path-> Configure Build Path" -> "Projects", then to add the   "TestDescriber-JaCoCo" project
5. Configure the source Test Case and Target classes (see description in section below)
6. Main_TD_2019: Run as Java Application (src/main/java ch.uzh.parser.testing.summarization)



## Real life OSS projects

### OfBiz
Download OfBiz source version apache-ofbiz-16.11.06. (https://www.apache.org/dyn/closer.lua/ofbiz/apache-ofbiz-16.11.06.zip)

Generate the ".project" file to allow Eclipse to automatically configure the project for you:
- Download and install Gradle
- Inside the ofbiz folder run "gradle wrapper" from the command line
- Run "gradlew eclipse" from the command line
- Run gradlew build

Import the project with "Existing Project into Workspace" 
https://cwiki.apache.org/confluence/display/OFBIZ/Eclipse+Tips

! needs path configuration



### Gson
Download gson in your workspace
https://github.com/google/gson

and import the project in Eclipse

![Image gson import](https://github.com/GLopezMUZH/SME19_TestDescriberProject/blob/master/documentation/imgs/gsonimport.png)


## Configure source Test Case and Target classes
There is a new class PathParameters for defining the Test Case Class as well as the targeted Source Code Class to be tested by the TC class. 

With the method createPathParameters the user can set both classes:

![Image 1](https://github.com/GLopezMUZH/SME19_TestDescriberProject/blob/master/documentation/imgs/2019-11-13_13h39_01.png)
