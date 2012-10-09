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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.authentication.PicketBoxConstants;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
@WebServlet (urlPatterns={"/signup"})
public class SignUpServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter("userId");
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        
        IdentityManager identityManager = getIdentityManager(req);
        
        if (identityManager.getUser(userId) != null) {
            req.getSession().setAttribute("message", "User ID already in use.");
            req.getRequestDispatcher("/signup.jsp").forward(req, resp);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            req.getSession().setAttribute("message", "Password mismatch.");
            req.getRequestDispatcher("/signup.jsp").forward(req, resp);
            return;
        }
        
        User user = identityManager.createUser(userId);
        
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        
        identityManager.updatePassword(user, password);
        
        Role guestRole = identityManager.createRole("guest");
        
        identityManager.grantRole(guestRole, user, null);
        
        resp.sendRedirect("login.jsp");
    }

    private IdentityManager getIdentityManager(HttpServletRequest req) {
        PicketBoxManager picketBoxManager = (PicketBoxManager) req.getServletContext().getAttribute(PicketBoxConstants.PICKETBOX_MANAGER);
        IdentityManager identityManager = picketBoxManager.getIdentityManager();
        return identityManager;
    }
    
}
