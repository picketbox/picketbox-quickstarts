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

package org.picketbox.http.quickstarts.idm;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.picketbox.core.PicketBoxManager;
import org.picketbox.http.PicketBoxConstants;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.PlainTextPassword;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleRole;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

/**
 * <p>
 * JAX-RS Endpoint to register users.
 * </p>
 * <p>
 * This class demonstrates how to inject and use the {@link IdentityManager} instance to manage users, groups and roles.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
@Path("/register")
public class RegistrationEndpoint {
    
    @Context
    private ServletContext servletContext;
    
    /**
     * <p>
     * Creates a new user using the information provided by the {@link RegistrationRequest} instance.
     * </p>
     * 
     * @param request
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RegistrationResponse register(final RegistrationRequest request) {
        RegistrationResponse response = new RegistrationResponse();

        if (!validateUserInformation(request)) {
            response.setStatus("All fields are required.");
            return response;
        }
        
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            response.setStatus("Password mismatch.");
            return response;
        }

        if (isUsernameAlreadyInUse(request)) {
            User user = new SimpleUser(request.getUserName());

            user.setFirstName(request.getFirstName());
            user.setEmail(request.getEmail());
            user.setLastName(request.getLastName());
            
            IdentityManager identityManager = getIdentityManager();
            
            identityManager.add(user);
            
            PlainTextPassword password = new PlainTextPassword(request.getPassword().toCharArray());
            
            // disable encoding for DIGEST authentication
            password.setEncodePassword(false);
            
            identityManager.updateCredential(user, password);
            
            Role roleGuest = identityManager.getRole("guest");
            
            if (roleGuest == null) {
                roleGuest = new SimpleRole("guest");
                identityManager.add(roleGuest);                
            }

            identityManager.grantRole(user, roleGuest);

            response.setStatus("Success");
        } else {
            response.setStatus("This username is already in use. Choose another one.");
        }

        return response;
    }

    /**
     * <p>
     * Checks if the provided username is already in use.
     * </p>
     * 
     * @param request
     * @return
     */
    private boolean isUsernameAlreadyInUse(final RegistrationRequest request) {
        return getIdentityManager().getUser(request.getUserName()) == null;
    }

    /**
     * <p>
     * Checks if the provided informations are valid.
     * </p>
     * 
     * @param request
     * @return
     */
    public boolean validateUserInformation(RegistrationRequest request) {
        String username = request.getUserName();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();
        String email = request.getEmail();
        String firstName = request.getFirstName();
        String lastName = request.getLastName();

        return !("".equals(username.trim()) || "".equals(password.trim()) || "".equals(confirmPassword.trim()) || "".equals(firstName.trim())
                || "".equals(lastName.trim()) || "".equals(email.trim()));
    }
    
    /**
     * <p>
     * Retrieve the {@link PicketBoxManager} instance from the {@link ServletContext} and get the configured
     * {@link IdentityManager}.
     * </p>
     * 
     * @param req
     * @return
     */
    private IdentityManager getIdentityManager() {
        PicketBoxManager picketBoxManager = (PicketBoxManager) this.servletContext
                .getAttribute(PicketBoxConstants.PICKETBOX_MANAGER);
        return picketBoxManager.getIdentityManager();
    }
}