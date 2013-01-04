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

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * Simple {@link Servlet} that uses the configured {@link IdentityManager} to create/register new users.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
@WebServlet(urlPatterns = { "/signup" })
public class SignUpServlet extends HttpServlet {

    private static final String ROLE_GUEST = "guest";
    private static final long serialVersionUID = 7251985700185294184L;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        
        User user = new SimpleUser(username);

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        
        String validationMessage = validateUser(user);
        
        if (validationMessage == null) {
            validationMessage = validatePassword(password, confirmPassword);            
        }
        
        if (validationMessage != null) {
            req.getSession().setAttribute("message", validationMessage);
            req.getRequestDispatcher("/signup.jsp").forward(req, resp);
        } else {
            IdentityManager identityManager = getIdentityManager(req);
            
            // creates the user
            identityManager.add(user);

            // updates user's password
            PlainTextPassword credential = new PlainTextPassword(password.toCharArray());

            identityManager.updateCredential(user, credential);

            // creates the default role
            Role guestRole = identityManager.getRole(ROLE_GUEST);

            if (guestRole == null) {
                guestRole = new SimpleRole(ROLE_GUEST);
                identityManager.add(guestRole);
            }

            // grant role guest to this user
            identityManager.grantRole(user, guestRole);

            resp.sendRedirect("login.jsp?signin=true");  
        }
    }

    private String validatePassword(String password, String confirmation) {
        String validationMessage = null;
        
        if ("".equals(password.trim())) {
            validationMessage = "Your Password is required.";
        }

        if (!password.equals(confirmation)) {
            validationMessage = "Password mismatch.";
        }
        
        return validationMessage;
    }
    
    private String validateUser(User user) {
        String validationMessage = null;

        if (user.getId() == null || "".equals(user.getId().trim())) {
            validationMessage = "Choose a User ID.";
        } else if (user.getFirstName() == null || "".equals(user.getFirstName().trim())) {
            validationMessage = "Your First Name is required.";
        } else if (user.getLastName() != null || "".equals(user.getLastName().trim())) {
            validationMessage = "Your Last Name is required.";
        } else if (user.getEmail() != null || "".equals(user.getEmail().trim())) {
            validationMessage = "Your Email is required.";
        }
        
        return validationMessage;
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
    private IdentityManager getIdentityManager(HttpServletRequest req) {
        PicketBoxManager picketBoxManager = (PicketBoxManager) req.getServletContext().getAttribute(
                PicketBoxConstants.PICKETBOX_MANAGER);
        return picketBoxManager.getIdentityManager();
    }

}
