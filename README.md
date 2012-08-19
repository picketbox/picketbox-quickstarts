PicketBox Quickstarts
==============

What is it?
-----------

This repository contains some useful examples about how to use PicketBox features.

Deploying the quickstarts
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