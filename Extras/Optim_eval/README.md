## Prerequisites

Running the code:
 - [Java 1.8 SE JRE](https://www.oracle.com/au/java/technologies/javase-jre8-downloads.html)

Compiling the code:
 - [Java 1.8 SE JDK](https://www.oracle.com/au/java/technologies/javase/javase-jdk8-downloads.html)
 - [Apache Maven](https://maven.apache.org/download.cgi)


## Building the code

Compile the code with your favourite IDE, or package `.jar` with Maven:

    mvn package

Running the instance schedule evaluation:

    java -jar target/evaluate_instance.jar <path_to_instance> <path_to_schedule>

Examples:

    java -jar target/evaluate_instance.jar instances/demoinstance.txt instances/demovalid.txt
    java -jar target/evaluate_instance.jar instances/demoinstance.txt instances/demoinvalid.txt


## Core classes

Main class to start from is found in `edu.monash.ppoi.EvaluateSchedule`. To fit the
local execution environment, change the filenames in `edu.monash.ppoi.Paths` to point
to your input datafiles.

  * `edu.monash.ppoi.instance` contains parser and data classes for instances.
  * `edu.monash.ppoi.solution` contains parser and data classes for solutions.
  * `edu.monash.ppoi.checker` contains classes for checking solution validity and value.
