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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;

import org.picketbox.core.authentication.AuthenticationCallbackHandler;
import org.picketbox.core.authentication.AuthenticationClient;
import org.picketbox.core.authentication.AuthenticationInfo;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.AuthenticationStatus;
import org.picketbox.core.authentication.impl.AbstractAuthenticationClient;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class TestDIGESTMD5Client extends AbstractAuthenticationClient implements AuthenticationClient {

    private static final String DIGEST = "DIGEST-MD5";
    private static final String REALM_PROPERTY = "com.sun.security.sasl.digest.realm";

    private int step = 1;

    /*
     * (non-Javadoc)
     * 
     * @see org.picketbox.core.authentication.api.AuthenticationClient#getRequiredCallbacks()
     */
    public List<AuthenticationInfo> getAuthenticationInfo() {
        ArrayList<AuthenticationInfo> callbacks = new ArrayList<AuthenticationInfo>();

        callbacks.add(new AuthenticationInfo("Client Authentication Info Callback", "Informations needed during the authentication.",
                SASLAuthenticationCallbackHandler.class));

        return callbacks;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.picketbox.core.authentication.api.AuthenticationClient#authenticate()
     */
    public AuthenticationResult authenticate(AuthenticationCallbackHandler handler) {
        RealmCallback realmCallback = new RealmCallback();
        AuthorizationIDCallback authorizationIDCallback = new AuthorizationIDCallback();
        AuthenticationIDCallBack authenticationIDCallBack = new AuthenticationIDCallBack();
        ProtocolCallback protocolCallback = new ProtocolCallback();
        ServerNameCallback serverNameCallback = new ServerNameCallback();
        PasswordCallback passwordCallback = new PasswordCallback("no-prompt", false);
        InputStreamCallback inputStreamCallback = new InputStreamCallback();
        OutputStreamCallback outputStreamCallback = new OutputStreamCallback();

        try {
            handler.handle(new Callback[] { inputStreamCallback, outputStreamCallback, realmCallback, authenticationIDCallBack,
                    authorizationIDCallback, protocolCallback, serverNameCallback, passwordCallback });
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (UnsupportedCallbackException e1) {
            e1.printStackTrace();
        }

        AuthenticationResult result = new AuthenticationResult();
        
        try {
            CallbackHandler clientCallback = new ClientCallbackHandler(authenticationIDCallBack.getAuthenticationId(),
                    passwordCallback.getPassword());
            SaslClient client = Sasl.createSaslClient(new String[] { DIGEST }, authorizationIDCallback.getAuthorizationId(),
                    protocolCallback.getProtocol(), serverNameCallback.getServerName(),
                    Collections.<String, Object> emptyMap(), clientCallback);

            AuthenticationStatus status = AuthenticationStatus.CONTINUE;
            
            while (status.equals(AuthenticationStatus.CONTINUE)) {
                byte[] message = null;

                byte[] buffer = new byte[1024];
                
                inputStreamCallback.getInputStream().read(buffer);

                message = new String(buffer).trim().getBytes();

                System.out.println("Client Response: " + new String(message));

                message = client.evaluateChallenge(message);

                if (client.isComplete()) {
                    status = AuthenticationStatus.SUCCESS;
                } else {
                    outputStreamCallback.getOutputStream().write(message);
                    outputStreamCallback.getOutputStream().flush();
                }
            }
        } catch (Exception e) {
            return authenticationFailed(result);
        }
        
        return performSuccessfulAuthentication(result);
    }

}
