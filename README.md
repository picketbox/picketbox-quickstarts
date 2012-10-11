PicketBox Quickstarts
==============

Introduction
-----------

The quickstarts included in this distribution were written to demonstrate PicketBox. They provide small, specific, working examples that can be used as a reference for your own project.

These quickstarts run on JBoss AS 7.

Be sure to read this entire document before you attempt to work with the quickstarts. It contains the following information:

* [Suggested Approach to the Quickstarts](#suggestedApproach): A suggested approach on how to work with the quickstarts.

* [System Requirements](#systemrequirements): List of software required to run the quickstarts.

* [Run the Quickstarts](#runningquickstarts): General instructions for building, deploying, and running the quickstarts.

<a id="suggestedApproach"></a>
Suggested Approach to the Quickstarts 
--------------------------------------

We suggest you approach the quickstarts as follows:

* Regardless of your level of expertise, we suggest you start with the **simple-http-form-auth** quickstart. It is the simplest example and is an easy way to prove your server is configured and started correctly.
* We recommend that you look [PicketBox documentation](https://docs.jboss.org/author/display/SECURITY/) to understand some basic concepts. These are important to make the quickstarts more comprehensible.
* Read the documentation for each quickstart before using it. You can find useful description and additional information about how to use it or what it demonstrates.

<a id="systemrequirements"></a>
System Requirements 
-------------

To run these quickstarts with the provided build scripts, you need the following:

1. Java 1.6, to run JBoss AS and Maven. You can choose from the following:
    * OpenJDK
    * Oracle Java SE
    * Oracle JRockit

2. Maven 3.0.0 or newer, to build and deploy the examples
    * If you have not yet installed Maven, see the [Maven Getting Started Guide](http://maven.apache.org/guides/getting-started/index.html) for details.
    * If you have installed Maven, you can check the version by typing the following in a command line:

            mvn --version 

3. The JBoss AS 7 distribution ZIP.
    * For information on how to install and run JBoss, refer to the documentation.

4. An HTML5 compatible browser such as Chrome, Safari 5+, Firefox 5+, or IE 9+ are recommended.

5. You can also use [JBoss Developer Studio or Eclipse](#useeclipse) to run the quickstarts.

With the prerequisites out of the way, you're ready to build and deploy.

<a id="runningquickstarts"></a>
Run the Quickstarts 
-------------------

The root folder of each quickstart contains a README file with specific details on how to build and run the example. In most cases you do the following:

* [Start the JBoss server](#startjboss)
* [Build and deploy the quickstart](#buildanddeploy)

<a id="startjboss"></a>
### Start the JBoss AS 7 Server

Before you deploy a quickstart, you need a running server 

The README for each quickstart will specify which configuration is required to run the example.

#### Start JBoss AS 7

1. Open a command line and navigate to the root of the JBoss server directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   JBOSS_HOME/bin/standalone.sh
        For Windows: JBOSS_HOME\bin\standalone.bat

<a id="buildanddeploy"></a>
### Build and Deploy the Quickstarts 

Review the README file in the root folder of the quickstart for specific details on how to build and run the example. In most cases you do the following:

1. The command used to build the quickstart depends on the individual quickstart, the server version, and how you configured Maven.

2. See the README file in each individual quickstart folder for specific details and information on how to run and access the example.

#### Build the Quickstart Archive

In some cases, you may want to build the application to test for compile errors or view the contents of the archive. 

1. Open a command line and navigate to the root directory of the quickstart you want to build.
2. Use this command if you only want to build the archive, but not deploy it:
	
		mvn clean package

#### Build and Deploy the Quickstart Archive

1. Make sure you [start the JBoss Server](#startjboss) as described in the README.
2. Open a command line and navigate to the root directory of the quickstart you want to run.
3. Use this command to build and deploy the archive:

		mvn clean package jboss-as:deploy

#### Undeploy an Archive

The command to undeploy the quickstart is simply: 

	mvn jboss-as:undeploy