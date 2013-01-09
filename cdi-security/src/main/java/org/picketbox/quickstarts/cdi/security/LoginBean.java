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

package org.picketbox.quickstarts.cdi.security;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.picketbox.cdi.LoginCredential;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketlink.Identity;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
@RequestScoped
@Named
public class LoginBean {

    private static final long serialVersionUID = 1L;

    @Inject
    private Identity identity;

    @Inject
    private LoginCredential credential;

    public String login(String userName, String password) {
        this.credential.setCredential(new UsernamePasswordCredential(userName, password));

        this.identity.login();

        if (this.identity.isLoggedIn()) {
            return "/private/index.xhtml";
        }

        return null;
    }
}
