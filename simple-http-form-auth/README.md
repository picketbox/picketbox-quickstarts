HTTP FORM Authentication Example
===================

What is it?
-----------

This example demonstrates how to enable HTTP FORM Authentication in web applications.

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