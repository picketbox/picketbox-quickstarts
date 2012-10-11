HTTP FORM and Database Authentication using a JPA-based Identity Store
===================

What is it?
-----------

This example demonstrates how to authenticate users using a HTTP FORM scheme and a JPA-based(database) Identity Store.  

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
	
And a simple filter to propagate the JPA EntityManager and make it available to PicketBox

	<filter>
		<filter-name>EntityManager Propagation Filter</filter-name>
		<filter-class>org.picketbox.quickstarts.filter.EntityManagerPropagationFilter</filter-class>
	</filter>
	
All the PicketBox configuration is done *with org.picketbox.quickstarts.configuration.CustomConfigurationPovider*. Like which resources should be protected, how they should be protected, etc.

Deploy and access the quickstart
-----------

To deploy this quickstart follow the instructions at the README file located at the project root directory.

You can access the quickstart using the following URL:

	http://localhost:8080/simple-http-form-auth/