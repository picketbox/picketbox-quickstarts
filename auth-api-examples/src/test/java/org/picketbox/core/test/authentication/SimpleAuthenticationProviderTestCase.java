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

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.authentication.handlers.UsernamePasswordAuthHandler;
import org.picketbox.core.authentication.manager.PropertiesFileBasedAuthenticationManager;
import org.picketbox.core.authorization.Resource;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.exceptions.AuthorizationException;
import org.picketbox.core.identity.IdentityManager;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class SimpleAuthenticationProviderTestCase {

    private PicketBoxManager picketboxManager;

    @Before
    public void onSetup() {
        PicketBoxConfiguration configuration = new PicketBoxConfiguration();

        configuration.identityManager(new IdentityManager() {
            
            @Override
            public PicketBoxSubject getIdentity(PicketBoxSubject resultingSubject) {
                
                resultingSubject.setRoleNames(new ArrayList<String>());
                
                resultingSubject.getRoleNames().add("Manager");
                resultingSubject.getRoleNames().add("Financial");
                
                return resultingSubject;
            }
        });
        
        configuration.authentication().addAuthManager(new PropertiesFileBasedAuthenticationManager());
        configuration.authorization(new MockAuthorizationManager() {
            
            @Override
            public boolean authorize(Resource resource, PicketBoxSubject subject) throws AuthorizationException {
                MockResource mockResource = (MockResource) resource;
                
                return mockResource.getName().equals("protectedResource");
            }
        });
        
        this.picketboxManager = configuration.buildAndStart();
    }
    
    @Test
    public void testSimpleAuthentication() throws AuthenticationException {
        PicketBoxSubject authenticatedSubject = this.picketboxManager.authenticate(new UsernamePasswordAuthHandler("admin", "admin"));
        
        Assert.assertNotNull(authenticatedSubject);
        Assert.assertTrue(authenticatedSubject.isAuthenticated());
        Assert.assertFalse(authenticatedSubject.getRoleNames().isEmpty());
        Assert.assertEquals("Manager", authenticatedSubject.getRoleNames().get(0));
        Assert.assertEquals("admin", authenticatedSubject.getUser().getName());
        Assert.assertNotNull(authenticatedSubject.getSession());
    }

    @Test
    public void testSimpleAuthorization() throws AuthenticationException {
        PicketBoxSubject authenticatedSubject = this.picketboxManager.authenticate(new UsernamePasswordAuthHandler("admin", "admin"));
    }

}
