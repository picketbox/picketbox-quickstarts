CDI Security Example
===================

What is it ?
-----------

This example demonstrates how to enable security for CDI Applications using PicketBox CDI extensions.  

This example uses CDI, JSF 2.0 + Richfaces 4 and JPA.

Security Configuration
-----------

All resources located at */cdi-security/private/* are protected by default. This is done by a security filter called *org.picketbox.quickstarts.cdi.security.SecurityFilter*.

You'll also notice that we defined at CDI Interceptor at /WEB-INF/beans.xml:

   	<interceptors>
		<class>org.apache.deltaspike.security.impl.extension.SecurityInterceptor</class>
	</interceptors>
	
This interceptor is required if you want to authorize your users using some of the provided annotations such as *@RolesAllowed* or *@UserLoggedIn*. You can also create your own annotations to restrict access to protected beans or methods.

Authentication
-----------

Users are authenticated by invoking the *org.picketbox.quickstarts.cdi.security.LoginBean*. The login page is located at  /src/main/webapp/login.xhtml.

After submitting the login form, the LoginBean populates the *LoginCredential* with the user credentials (username and password) and then performs the authentication by invoking the Identity.login() method.

User Self-Registration
-----------

PicketBox is fully integrated with PicketLink IDM. That said, this example uses the IDM features to create a self registration page.

Users are created by invoking the *org.picketbox.quickstarts.cdi.security.SignUpBean*. This bean uses the *IdentityManager* to store the user in the database.

During the registration the user is granted a role "guest" and a group "PicketBox Users".

Persistence Configuration
-----------

The datasource is located at 

	/src/main/webapp/WEB-INF/cdi-security-ds.xml
	
It is automatically deployed when you deploy the quickstart. If you are running in a production environment, add a managed data source, this example data source is just for development and testing !

The persistence.xml is located at

	/src/main/resources/META-INF/persistence.xml
	
Another important thing is how you configure PicketBox to use the JPA Identity Store. This is done by the *org.picketbox.quickstarts.configuration.SecurityConfigurationProducer*

	ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        
	// configures a JPA-based identity store.
    configurationBuilder
    	.identityManager()
    		.jpaStore(this.jpaTemplate);

The *org.picketbox.cdi.idm.DefaultJPATemplate* is used to automatically get the EntityManager using CDI. This is necessary to allow the Identity Manager to persist your users, roles, groups and etc.

Deploy and access the quickstart
-----------

To deploy this quickstart follow the instructions at the README file located at the project root directory.

You can access the quickstart using the following URL:

	http://localhost:8080/cdi-security/