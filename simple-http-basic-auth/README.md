HTTP BASIC Authentication Example
===================

What is it?
-----------

This example demonstrates how to enable HTTP BASIC Authentication in web applications.

The example can be deployed using Maven from the command line or from Eclipse using JBoss Tools.

System requirements
-----------

All you need to build this project is Java 6.0 (Java SDK 1.6) or better, Maven 3.0 or better.

The application this project produces is designed to be run on JBoss AS 7 or JBoss Enterprise Application Platform 6.

An HTML5 compatible browser such as Chrome, Safari 5+, Firefox 5+, or IE 9+ are required.

With the prerequisites out of the way, you're ready to build and deploy.

Deploying the application
-----------

### Deploying locally

First you need to start the JBoss container. To do this, run

	$JBOSS_HOME/bin/standalone.sh

or if you are using windows

	$JBOSS_HOME/bin/standalone.bat

To deploy the application, you first need to produce the archive to deploy using the following Maven goal:

	mvn package

You can now deploy the artifact by executing the following command:

	mvn jboss-as:deploy

This will deploy both the client and service applications.

The application will be running at the following URL http://localhost:8080/${artifactId}/.

To undeploy run this command:

	mvn jboss-as:undeploy