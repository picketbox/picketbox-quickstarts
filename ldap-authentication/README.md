HTTP FORM and LDAP Authentication using a LDAP-based Identity Store
===================

What is it ?
-----------

This example demonstrates how to authenticate users using a HTTP FORM scheme and a LDAP-based Identity Store.  

You can check the configuration by looking at the web deployment descriptor located at:

	src/main/webapp/WEB-INF/web.xml

The configuration is done by defining a context parameter to configure HTTP FORM Authentication

	<context-param>
		<param-name>org.picketbox.authentication</param-name>
		<param-value>FORM</param-value>
	</context-param>
	
Another context parameter to define the PicketBox Configuration Provider implementation

	<context-param>
		<param-name>org.picketbox.configuration.provider</param-name>
		<param-value>org.picketbox.quickstarts.configuration.CustomConfigurationPovider</param-value>
	</context-param>

The PicketBox Security filter definition

	<filter>
		<filter-name>PicketBox Delegating Filter</filter-name>
		<filter-class>org.picketbox.http.filters.DelegatingSecurityFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>PicketBox Delegating Filter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	
All the PicketBox configuration is done with *org.picketbox.quickstarts.configuration.CustomConfigurationPovider*. Like which resources should be protected, how they should be protected, etc.

LDAP Configuration
-----------

This quickstart have a Embbeded ApacheDS LDAP Server. When the application is deployed the server is automatically started by the 

	org.picketbox.quickstarts.listener.LDAPServerInitializationListener

During the server startup the directory is populated with some default informations. You can check the LDIF file at 

	src/main/resources/ldap/users.ldif
	
Another important thing is how you configure PicketBox to use the LDAP Identity Store. This is done by the *org.picketbox.quickstarts.configuration.CustomConfigurationPovider*

	HTTPConfigurationBuilder configurationBuilder = new HTTPConfigurationBuilder();
        
	// configures a LDAP-based identity store.
    configurationBuilder
            .identityManager()
                .ldapStore()
                    .url("ldap://localhost:10389/")
                    .bindDN("uid=jduke,ou=People,dc=jboss,dc=org")
                    .bindCredential("theduke")
                    .userDNSuffix("ou=People,dc=jboss,dc=org")
                    .groupDNSuffix("ou=Groups,dc=jboss,dc=org")
                    .roleDNSuffix("ou=Roles,dc=jboss,dc=org");
    
Deploy and access the quickstart
-----------

To deploy this quickstart follow the instructions at the README file located at the project root directory.

You can access the quickstart using the following URL:

	http://localhost:8080/ldap-authentication/