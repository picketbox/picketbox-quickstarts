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

package org.picketbox.core.test.authentication;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.DefaultPicketBoxManager;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationProvider;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.AuthenticationService;
import org.picketbox.core.authentication.handlers.UsernamePasswordAuthHandler;
import org.picketbox.core.authentication.impl.UserNamePasswordMechanism;
import org.picketbox.core.authentication.manager.PropertiesFileBasedAuthenticationManager;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.exceptions.AuthenticationException;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class SimpleAuthenticationProviderTestCase {

    private DefaultPicketBoxManager picketboxManager;

    @Before
    public void onSetup() {
        PicketBoxConfiguration configuration = new PicketBoxConfiguration();

        configuration.authentication().addMechanism(new UserNamePasswordMechanism());
        
        configuration.authentication().addAuthManager(new PropertiesFileBasedAuthenticationManager());
        
        this.picketboxManager = (DefaultPicketBoxManager) configuration.buildAndStart();
    }
    
    @Test
    public void testMechanismConfiguration() {
        AuthenticationProvider authenticationProvider = this.picketboxManager.getAuthenticationProvider();
        
        AuthenticationMechanism mechanism = authenticationProvider.getMechanism(UserNamePasswordMechanism.class.getName());
        
        AuthenticationService service = mechanism.getService();
        
        try {
            AuthenticationResult result = service.authenticate(new UsernamePasswordAuthHandler("admin", "admin"));
            
            Assert.assertNotNull(result.getSubject());
            Assert.assertTrue(result.getSubject().isAuthenticated());
        } catch (AuthenticationException e) {
            e.printStackTrace();
            Assert.fail("Authentication failed.");
        }
    }

}
