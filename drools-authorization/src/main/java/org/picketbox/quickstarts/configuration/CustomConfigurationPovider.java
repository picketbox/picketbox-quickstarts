package org.picketbox.quickstarts.configuration;

import javax.servlet.ServletContext;

import org.picketbox.drools.authorization.PicketBoxDroolsAuthorizationManager;
import org.picketbox.http.config.ConfigurationBuilderProvider;
import org.picketbox.http.config.HTTPConfigurationBuilder;
import org.picketbox.http.resource.ProtectedResourceConstraint;

/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/**
 * <p>
 * Implementation for the {@link ConfigurationBuilderProvider} interface that demonstrates how to use the Configuration API to
 * configure web applications.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class CustomConfigurationPovider implements ConfigurationBuilderProvider {

    /*
     * (non-Javadoc)
     * 
     * @see org.picketbox.http.config.ConfigurationBuilderProvider#getBuilder(javax.servlet.ServletContext)
     */
    @Override
    public HTTPConfigurationBuilder getBuilder(ServletContext servletcontext) {
        HTTPConfigurationBuilder configurationBuilder = new HTTPConfigurationBuilder();
        
        // configures the Drools Authorization Manager
        configurationBuilder
            .authorization()
                .manager(new PicketBoxDroolsAuthorizationManager());
        
        // protected resources configuration
        configurationBuilder.protectedResource()
                // unprotected resource. Usually this will be your application's static resources like CSS, JS, etc.
                .resource("/resources/*", ProtectedResourceConstraint.NOT_PROTECTED)
                // the access denied error page should not be protected.
                .resource("/accessDenied.jsp", ProtectedResourceConstraint.NOT_PROTECTED)
                // the login page is marked as not protected.
                .resource("/login.jsp", ProtectedResourceConstraint.NOT_PROTECTED)
                // the register page is marked as not protected.
                .resource("/signup.jsp", ProtectedResourceConstraint.NOT_PROTECTED)
                // the user register resources is marked as not protected.
                .resource("/services/register", ProtectedResourceConstraint.NOT_PROTECTED)
                .resource("/services/checkUsername", ProtectedResourceConstraint.NOT_PROTECTED)
                // the error page is marked as not protected.
                .resource("/error.jsp", ProtectedResourceConstraint.NOT_PROTECTED)
                // define a specific protected resource. This authorization will be done by Drools.
                .resource("/droolsProtectedResource.jsp", "guest")
                // defines that all resources should require AUTHENTICATION. They will be available only for users with a role named 'guest'.
                .resource("/*", ProtectedResourceConstraint.AUTHENTICATION, "guest");

        return configurationBuilder;
    }

}
