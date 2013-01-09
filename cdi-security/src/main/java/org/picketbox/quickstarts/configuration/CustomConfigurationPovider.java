package org.picketbox.quickstarts.configuration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.picketbox.cdi.idm.DefaultJPATemplate;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.http.config.ConfigurationBuilderProvider;
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
@ApplicationScoped
public class CustomConfigurationPovider {

    @Inject
    private DefaultJPATemplate jpaTemplate;

    @Produces
    public ConfigurationBuilder configure() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

        // configures a JPA-based identity store.
        configurationBuilder.identityManager().jpaStore().template(this.jpaTemplate);

        return configurationBuilder;
    }

}
