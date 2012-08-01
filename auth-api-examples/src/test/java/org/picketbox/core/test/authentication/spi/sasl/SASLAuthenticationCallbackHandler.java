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

package org.picketbox.core.test.authentication.spi.sasl;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;

import org.picketbox.core.authentication.impl.AbstractAuthenticationCallbackHandler;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class SASLAuthenticationCallbackHandler extends AbstractAuthenticationCallbackHandler {

    private String userId;
    private String password;
    private String authorizationId;
    private String protocol;
    private String server;
    private String realm;
    private Principal principal;
    private InputStream inputStream;
    private OutputStream outputStream;

    public SASLAuthenticationCallbackHandler(InputStream inputStream, OutputStream outputStream,
            String protocol, String server, String realm) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.protocol = protocol;
        this.server = server;
        this.realm = realm;
    }

    public SASLAuthenticationCallbackHandler(InputStream inputStream, OutputStream outputStream, String authenticationId, String authorizationId, String password,
            String protocol, String server, String realm) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.userId = authenticationId;
        this.password = password;
        this.authorizationId = authorizationId;
        this.protocol = protocol;
        this.server = server;
        this.realm = realm;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the authenticationId
     */
    public String getAuthorizationId() {
        return authorizationId;
    }

    /**
     * @param authenticationId the authenticationId to set
     */
    public void setAuthorizationId(String authenticationId) {
        this.authorizationId = authenticationId;
    }

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the server
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the realm
     */
    public String getRealm() {
        return realm;
    }

    /**
     * @param realm the realm to set
     */
    public void setRealm(String realm) {
        this.realm = realm;
    }

    public List<Class<? extends Callback>> getSupportedCallbacks() {
        return null;
    }

    /**
     * @return the principal
     */
    public Principal getPrincipal() {
        return principal;
    }

    /**
     * @param principal the principal to set
     */
    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    @Override
    protected void doHandle(Callback callback) throws UnsupportedCallbackException {
        if (callback instanceof AuthorizeCallback) {
            AuthorizeCallback acb = (AuthorizeCallback) callback;
            acb.setAuthorized(acb.getAuthenticationID().equals(acb.getAuthorizationID()));
        }
        if (callback instanceof RealmCallback) {
            RealmCallback realm = (RealmCallback) callback;

            realm.setRealm(this.realm);
        }
        if (callback instanceof PasswordCallback) {
            PasswordCallback password = (PasswordCallback) callback;
            if (this.password != null) {
                password.setPassword(this.password.toCharArray());
            }
        }
        if (callback instanceof ProtocolCallback) {
            ProtocolCallback protocol = (ProtocolCallback) callback;

            protocol.setProtocol(this.protocol);
        }
        if (callback instanceof AuthenticationIDCallBack) {
            AuthenticationIDCallBack authId = (AuthenticationIDCallBack) callback;

            authId.setAuthenticationId(this.userId);
        }
        if (callback instanceof AuthorizationIDCallback) {
            AuthorizationIDCallback authzId = (AuthorizationIDCallback) callback;

            authzId.setAuthorizationId(this.authorizationId);
        }
        if (callback instanceof ServerNameCallback) {
            ServerNameCallback server = (ServerNameCallback) callback;

            server.setServerName(this.server);
        }
        if (callback instanceof InputStreamCallback) {
            InputStreamCallback inputStreamCallback = (InputStreamCallback) callback;

            inputStreamCallback.setInputStream(inputStream);
        }
        if (callback instanceof OutputStreamCallback) {
            OutputStreamCallback outPutStreamCallback = (OutputStreamCallback) callback;

            outPutStreamCallback.setOutputStream(this.outputStream);
        }        
    }

}
