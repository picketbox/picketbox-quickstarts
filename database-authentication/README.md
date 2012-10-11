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
	
The filter above is not required by PicketBox. Usually you would use some framework that already provide such capabilities.  
	
All the PicketBox configuration is done with *org.picketbox.quickstarts.configuration.CustomConfigurationPovider*. Like which resources should be protected, how they should be protected, etc.

Persistence Configuration
-----------

The datasource is located at 

	/src/main/webapp/WEB-INF/database-auth-quickstart-ds.xml
	
It is automatically deployed when you deploy the quickstart. If you are running in a production environment, add a managed data source, this example data source is just for development and testing !

The persistence.xml is located at

	/src/main/resources/META-INF/persistence.xml
	
Another important thing is how you configure PicketBox to use the JPA Identity Store. This is done by the *org.picketbox.quickstarts.configuration.CustomConfigurationPovider*

	HTTPConfigurationBuilder configurationBuilder = new HTTPConfigurationBuilder();
        
	// configures a JPA-based identity store.
    configurationBuilder
    	.identityManager()
    		.jpaStore();
    
By default, the JPA-based Identity Store expects to get the EntityManager from a ThreadLocal. That is why we need the EntityManagerPropagationFilter.

You can always define your own strategy about how the EntityManager is available to the store. Just create a custom implementation of *org.picketlink.idm.internal.jpa.JPATemplate* and configure as follow

	HTTPConfigurationBuilder configurationBuilder = new HTTPConfigurationBuilder();
        
	// configures a JPA-based identity store.
    configurationBuilder
    	.identityManager()
    		.jpaStore()
    			.template(new MyCustomJPATemplate());	

Deploy and access the quickstart
-----------

To deploy this quickstart follow the instructions at the README file located at the project root directory.

You can access the quickstart using the following URL:

	http://localhost:8080/simple-http-form-auth/