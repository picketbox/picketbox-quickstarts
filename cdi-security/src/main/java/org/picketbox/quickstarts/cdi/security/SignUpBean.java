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

import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.PlainTextPassword;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleGroup;
import org.picketlink.idm.model.SimpleRole;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
@Stateless
@Named
public class SignUpBean {

    @Inject
    private IdentityManager identityManager;

    @Inject
    private NewUser newUser;

    @Inject
    private FacesContext facesContext;

    public String signup() {
        if (isUserNameInUser()) {
            this.facesContext.addMessage(null, new FacesMessage("User ID already in use. Please, choose another one."));
        } else {
            if (!newUser.getPassword().equals(newUser.getPasswordConfirmation())) {
                this.facesContext.addMessage(null, new FacesMessage("Password mismatch."));
            }

            User user = new SimpleUser(this.newUser.getUserName());

            user.setFirstName(this.newUser.getFirstName());
            user.setLastName(this.newUser.getLastName());
            user.setEmail(this.newUser.getEmail());

            this.identityManager.add(user);

            PlainTextPassword password = new PlainTextPassword(this.newUser.getPassword().toCharArray());

            this.identityManager.updateCredential(user, password);
            
            Role guest = this.identityManager.getRole("guest");
            
            if (guest == null) {
                guest = new SimpleRole("guest");
                this.identityManager.add(guest);
            }
            
            this.identityManager.grantRole(user, guest);
            
            Group group = this.identityManager.getGroup("PicketBox Users");
            
            if (group == null) {
                group = new SimpleGroup("PicketBox Users");
                this.identityManager.add(group);
            }
            
            this.identityManager.addToGroup(user, group);
            
            return "/login.xhtml";
        }
        
        return null;
    }

    private boolean isUserNameInUser() {
        return this.identityManager.getUser(this.newUser.getUserName()) != null;
    }

}
