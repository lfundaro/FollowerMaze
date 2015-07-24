##Introduction
This is the Java implementation of the FollowerMaze challenge.

##Requirements

* JDK 1.7
* Maven 3.2.1+

##Configuration 
This project contains 2 configuration files. You don't need to modify them,
but in case you do, here's a brief explanation:

* logging.properties: contains all information about logging.
* config.properties: used to configure listening ports, queue sizes, etc. 

Both files are located under the resources folder of this project.

##Compile
"""
$> mvn clean install 
"""

## Run 
"""
$> cd target/ 
$> java -jar -server -Djava.util.logging.config.file=classes/logging.properties FollowerMaze-1.0.jar
"""

##Stop
"""
$> ^C
"""