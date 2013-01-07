HTTP CLIENT-CERT Authentication Example
===================

What is it?
-----------

This example demonstrates how to enable HTTP CLIENT_CERT Authentication in web applications.

You can check the configuration by looking at the web deployment descriptor located at:

	src/main/webapp/WEB-INF/web.xml

The configuration is done by defining a context parameter to configure HTTP CLIENT_CERT Authentication

	<context-param>
		<param-name>org.picketbox.authentication</param-name>
		<param-value>CLIENT_CERT</param-value>
	</context-param>
	
Another context parameter to define the PicketBox Configuration Provider implementation

	<context-param>
		<param-name>org.picketbox.configuration.provider</param-name>
		<param-value>org.picketbox.quickstarts.configuration.CustomConfigurationPovider</param-value>
	</context-param>

And the PicketBox Security filter definition

	<filter>
		<filter-name>PicketBox Delegating Filter</filter-name>
		<filter-class>org.picketbox.http.filters.DelegatingSecurityFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>PicketBox Delegating Filter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

All the PicketBox configuration is done with org.picketbox.quickstarts.configuration.CustomConfigurationPovider. Like which resources should be protected, how they should be protected, etc.

Configuring JBoss AS7 SSL+CLIENT-CERT
-----------

This quickstart uses CLIENT-CERT to authenticate users. That said, you need to properly configure your JBoss AS7 installation in order to setup SSL and to validate client certificates.

The steps bellow will show you the configuration steps.

### Generate a PKCS12 KeyStore

	keytool -genkeypair -alias servercert -keystore server.pfx -storepass servercert -validity 365 -keyalg RSA -keysize 2048 -storetype pkcs12
	
The command above will create a file called server.pfx.

### SSL Configuration

Edit your JBOSS_HOME/standalone/configuration/standalone.xml and add a new connector for SSL/HTTPS:

		<subsystem xmlns="urn:jboss:domain:web:1.1" default-virtual-server="default-host" native="false">
            
            ...
            
            <connector name="https" protocol="HTTP/1.1" scheme="https" socket-binding="https" secure="true">
                <ssl 	name="https" 
                		key-alias="servercert" 
                		password="servercert" 
                		certificate-key-file="/path_to_cert/server.pfx" 
                		protocol="TLSv1" verify-client="true" 
                		ca-certificate-file="/path_to_cert/server.pfx" 
                		keystore-type="PKCS12" 
                		truststore-type="PKCS12"/>
            </connector>

			...
			
        </subsystem>
        
Deploy and access the quickstart
-----------

To deploy this quickstart follow the instructions at the README file located at the project root directory.

You can access the quickstart using the following URL:

	https://localhost:8443/http-cert/index.jsp