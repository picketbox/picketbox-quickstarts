HTTP FORM and Resource Protection using Drools Authorization
===================

What is it ?
-----------

This example demonstrates how to control access to your application resources using Drools.  

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

Drools Configuration
-----------

The Drools rules are defined in the following file 

	/src/main/resources/authorization.drl
	
The file above defines a Drools rule that allows access to the */droolsProtectedResource.jsp* only for the user *droolsuser* (username) and role *guest*.

	rule "Authorize if principal == droolsuser with role == guest"
	dialect "java"
	no-loop
		when
	  		$principal : Principal( name == "droolsuser" )
	  		$resource : Resource()
	  		$userContext : UserContext (hasRole('guest'))
	 	then
	    	modify ($resource) {
	       		setAuthorized(true)
	    	};
	end

You can check the PicketBox configuration by checking the *org.picketbox.quickstarts.configuration.CustomConfigurationPovider* class

	HTTPConfigurationBuilder configurationBuilder = new HTTPConfigurationBuilder();
        
    // configures the Drools Authorization Manager
    configurationBuilder
        .authorization()
            .manager(new PicketBoxDroolsAuthorizationManager());
    
    // protected resources configuration
    configurationBuilder.protectedResource()
            // the access denied error page should not be protected.
            .resource("/accessDenied.jsp", ProtectedResourceConstraint.NOT_PROTECTED)
            // define a specific protected resource. This authorization will be done by Drools.
            .resource("/droolsProtectedResource.jsp", "guest")
            // defines that all resources should require AUTHENTICATION. They will be available only for users with a role named 'guest'.
            .resource("/*", ProtectedResourceConstraint.AUTHENTICATION, "guest");

The configuration above defines a custom PicketBox AuthorizationManager. In this case the *PicketBoxDroolsAuthorizationManager* implementation. This class is provided by the [PicketBox Drools](https://docs.jboss.org/author/display/SECURITY/Drools+Authorization): project.

We also define some additional configuration for the protected resources. 

First, the /accessDenied.jsp page is defined as not protected because it is public.

    // the access denied error page should not be protected.
    .resource("/accessDenied.jsp", ProtectedResourceConstraint.NOT_PROTECTED)

We also define a specific pattern that matches the /droosProtectedResource.jsp

	// define a specific protected resource. This authorization will be done by Drools.
    .resource("/droolsProtectedResource.jsp", "guest")
    
And finally, we define a configuration that matches all application resources and applying for them a constraint that they should require only AUTHENTICATION. If you do not use the AUTHENTICATION constraint PicketBox will understand that all resources should be authorized. We want authorization only for the droolsProtectedResource.jsp page. 
  

How to Use
-----------

Deploy the application and create a new user by clicking the *Register* button. Create an user with User ID *droolsuser*.

Now you can login and try to access the Drools protected resource using the following URL

	http://localhost:8080/drools-authorization/droolsProtectedResource.jsp
	
Try to create different users with a different User ID to see the access denied page.

Deploy and access the quickstart
-----------

To deploy this quickstart follow the instructions at the README file located at the project root directory.

You can access the quickstart using the following URL:

	http://localhost:8080/database-authentication/