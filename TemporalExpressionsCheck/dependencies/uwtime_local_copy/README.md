# UWTime#

## Introduction ##

UWTime is a temporal semantic parser that detects and resolves time expressions in text. The system is described in:

>[Context-dependent Semantic Parsing for Time Expressions ](http://homes.cs.washington.edu/~kentonl/pub/ladz-acl.2014.pdf)

>[Kenton Lee](http://homes.cs.washington.edu/~kentonl), [Yoav Artzi](http://yoavartzi.com/), [Jesse Dodge](http://www.cs.cmu.edu/afs/cs/Web/People/jessed) and [Luke Zettlemoyer](http://homes.cs.washington.edu/~lsz)

>*In Proceedings of the Conference of the Association for Computational Linguistics (ACL), 2014*

This repository contains a minimal, stand-alone version of UWTime intended for use by a downstream application. To replicate the results from the paper, please use https://bitbucket.org/kentonl/uwtime instead.

UWTime was implemented using [The University of Washington Semantic Parsing Framework](https://bitbucket.org/yoavartzi/spf). A demo of UWTime can be found at http://lil.cs.washington.edu/uwtime.

For questions and inquiries, please contact [Kenton Lee](mailto:kentonl@cs.washington.edu).

## Authors ##

Developed and maintained by [Kenton Lee](http://homes.cs.washington.edu/~kentonl)

Contributors: [Yoav Artzi](http://yoavartzi.com/), [Jesse Dodge](http://www.cs.cmu.edu/afs/cs/Web/People/jessed), [Luke Zettlemoyer](http://homes.cs.washington.edu/~lsz)

## Documentation ##
UWTime can be run from the command line, as a REST server, or via its Java API.

### Building ###
To build UWTime, run ```mvn package``` at the root directory. The output JAR file will be in the ```target``` directory.

### Command Line ###
To run UWTime from the command line, run the JAR file with the input sentence or document as the argument. For example, ```java -jar target/uwtime-standalone-1.0.1.jar "A document written today"```. The document creation time is set to the current date, and the domain is set to ```OTHER```. This functionality is only included for testing purpose. For batch processing, please use the either the server or API as described below.

### Server ###
To use UWTime as a REST server, run the JAR file using ```java -jar target/uwtime-1.0.1.jar``` without arguments. Clients can query the UWTime server by providing the document text ```query```, the document creation time ```dct```, and the domain ```domain```. A basic python script ```client.py``` is provided as an example.

### API ###
To access UWTime via its Java API:

1. Add the JAR file to the classpath. If you use Maven to manage your project dependencies, you can simply include the following in your ```pom.xml```:

        <dependency>
          <groupId>edu.uw.cs.lil</groupId>
          <artifactId>uwtime-standalone</artifactId>
          <version>1.0.1</version>
        </dependency>

2. Initialize UWTime by creating a TemporalAnnotator using ```TemporalAnnotator annotator = new TemporalAnnotator();```
3. Annotate a document by providing the document text, the document creation time, and the domain. For example, ```DocumentAnnotation da = annotator.extract("A document written today", "1970-01-01", TemporalDomain.OTHER);```
4. From the ```DocumentAnnotation``` object, you can retrieve the annotated TimeML document and mentions of time expressions, along with their logical forms, TIMEX3 values, confidences, etc.

An example of the usage of the API can be found in the ```TemporalHandler``` class.

## License ##
UWTime - Copyright (C) 2014 Kenton Lee

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.