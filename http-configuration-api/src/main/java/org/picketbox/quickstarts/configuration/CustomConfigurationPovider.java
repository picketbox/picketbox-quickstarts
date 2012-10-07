package org.picketbox.quickstarts.configuration;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.picketbox.core.identity.impl.ConfiguredRolesSubjectPopulator;
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
        
        configurationBuilder
            .identityManager().userPopulator(new ConfiguredRolesSubjectPopulator(getCommonRoles()));
            
        configurationBuilder
            // configure the protected resources. Note that the /confidentialResource will be unavailable for all users because they have only the "manager" role.
            .protectedResource()
                // unprotected resource. Usually this will be your application's static resources like CSS, JS, etc.
                .resource("/resources/*", ProtectedResourceConstraint.NOT_PROTECTED)
                // defines that only users with role "manager" can access it
                .resource("/onlyManagers.jsp", "manager")
                // defines that this resources should be accessed only by authenticated users. No authorization will be performed.
                .resource("/noAuthorization", ProtectedResourceConstraint.AUTHENTICATION)
                .resource("/confidentialResource.jsp", "confidential");
 
        return configurationBuilder;    
    }

    /**
     * <p>
     * Returns a {@link List} of roles common for all users.
     * </p>
     * 
     * @return
     */
    private List<String> getCommonRoles() {
        List<String> roles = new ArrayList<String>();

        roles.add("manager");

        return roles;
    }

}
