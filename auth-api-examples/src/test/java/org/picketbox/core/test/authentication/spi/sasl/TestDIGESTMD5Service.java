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

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslServer;

import org.picketbox.core.authentication.AuthenticationCallbackHandler;
import org.picketbox.core.authentication.AuthenticationInfo;
import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.DigestHolder;
import org.picketbox.core.authentication.impl.AbstractAuthenticationService;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.util.HTTPDigestUtil;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class TestDIGESTMD5Service extends AbstractAuthenticationService {

    public TestDIGESTMD5Service(AuthenticationMechanism mechanism) {
        super(mechanism);
    }

    private static final String DIGEST = "DIGEST-MD5";
    private SaslServer server;
    private SASLServerAuthHandler serverHandler;

    /*
     * (non-Javadoc)
     * 
     * @see org.picketbox.core.authentication.api.AuthenticationService#getAuthenticationInfo()
     */
    public List<AuthenticationInfo> getAuthenticationInfo() {
        ArrayList<AuthenticationInfo> callbacks = new ArrayList<AuthenticationInfo>();

        callbacks.add(new AuthenticationInfo("Client Authentication Info Callback", "Informations needed during the authentication.",
                SASLAuthenticationCallbackHandler.class));

        return callbacks;
    }

    @Override
    protected Principal doAuthenticate(AuthenticationManager authenticationManager,
            AuthenticationCallbackHandler callbackHandler, AuthenticationResult result) throws AuthenticationException {
        RealmCallback realmCallback = new RealmCallback();
        ProtocolCallback protocolCallback = new ProtocolCallback();
        ServerNameCallback serverNameCallback = new ServerNameCallback();
        InputStreamCallback inputStreamCallback = new InputStreamCallback();
        OutputStreamCallback outputStreamCallback = new OutputStreamCallback();

        try {
            callbackHandler
                    .handle(new Callback[] { inputStreamCallback, outputStreamCallback, realmCallback, protocolCallback, serverNameCallback});
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }

        try {
            if (this.server == null) {
                serverHandler = new SASLServerAuthHandler();
                server = Sasl.createSaslServer(DIGEST, protocolCallback.getProtocol(), serverNameCallback.getServerName(),
                        (Map) Collections.emptyMap(), serverHandler);
                outputStreamCallback.getOutputStream().write(server.evaluateResponse(new byte[0]));
                outputStreamCallback.getOutputStream().flush();
                
                // require more steps
                requireMoreSteps(result);
            } else {
                byte[] buffer = new byte[1024];

                System.out.println("Server waiting for message.");
                
                inputStreamCallback.getInputStream().read(buffer);
                
                String clientResponse = new String(buffer).trim();
                
                System.out.println("Server response: " + clientResponse);
                
                String[] tokens = HTTPDigestUtil.quoteTokenize(clientResponse);

                // temporary digest parsing. Just get the user name
                final DigestHolder digest = HTTPDigestUtil.digest(tokens);
                
                byte[] message = server.evaluateResponse(clientResponse.getBytes());
                
                outputStreamCallback.getOutputStream().write(message);
                outputStreamCallback.getOutputStream().flush();

                if (server.isComplete()) {
                    return new Principal() {
                        
                        public String getName() {
                            return digest.getUsername();
                        }
                    };
                }
            }
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
        
        return null;
    }

}
