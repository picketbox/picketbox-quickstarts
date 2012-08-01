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

package org.picketbox.core.test.authentication.spi.http;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.security.auth.callback.Callback;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.picketbox.core.authentication.AuthenticationCallbackHandler;
import org.picketbox.core.authentication.AuthenticationInfo;
import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.DigestHolder;
import org.picketbox.core.authentication.PicketBoxConstants;
import org.picketbox.core.authentication.impl.AbstractAuthenticationService;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.nonce.NonceGenerator;
import org.picketbox.core.nonce.UUIDNonceGenerator;
import org.picketbox.core.util.HTTPDigestUtil;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class HTTPDigestService extends AbstractAuthenticationService {

    private static final String PICKET_BOX_REALM = "PicketBox Realm";


    public HTTPDigestService(AuthenticationMechanism mechanism) {
        super(mechanism);
    }

    protected String opaque = UUID.randomUUID().toString();

    protected String qop = PicketBoxConstants.HTTP_DIGEST_QOP_AUTH;

    // How long is the nonce valid? By default, it is set at 3 minutes
    protected long nonceMaxValid = 3 * 60 * 1000;

    protected NonceGenerator nonceGenerator = new UUIDNonceGenerator();
    
    protected ConcurrentMap<String, List<String>> idVersusNonce = new ConcurrentHashMap<String, List<String>>();

    public List<AuthenticationInfo> getAuthenticationInfo() {
        List<AuthenticationInfo> info = new ArrayList<AuthenticationInfo>();
        
        info.add(new AuthenticationInfo("HTTP Digest Authentication", "Provides an HTTP Digest Authentication service", HTTPDigestAuthHandler.class));
        
        return info;
    }

    @Override
    protected Principal doAuthenticate(AuthenticationManager authenticationManager,
            AuthenticationCallbackHandler callbackHandler, AuthenticationResult result) throws AuthenticationException {
        HttpServletRequestCallback requestCallback = new HttpServletRequestCallback();
        HttpServletResponseCallback responseCallback = new HttpServletResponseCallback();
        
        
        try {
            callbackHandler.handle(new Callback[] {requestCallback, responseCallback});
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
        
        HttpServletRequest request = requestCallback.getRequest();
        HttpServletResponse response = responseCallback.getResponse();
        
        if (request.getHeader(PicketBoxConstants.HTTP_AUTHORIZATION_HEADER) == null) {
            requireMoreSteps(result);
            challengeClient(request, response);
            return null;
        }
        
        String sessionId = request.getSession().getId();
        
        // Get the Authorization Header
        String authorizationHeader = request.getHeader(PicketBoxConstants.HTTP_AUTHORIZATION_HEADER);

        if (authorizationHeader != null && authorizationHeader.isEmpty() == false) {

            if (authorizationHeader.startsWith(PicketBoxConstants.HTTP_DIGEST)) {
                authorizationHeader = authorizationHeader.substring(7).trim();
            }
            String[] tokens = HTTPDigestUtil.quoteTokenize(authorizationHeader);

            int len = tokens.length;
            if (len == 0) {
                return null;
            }

            DigestHolder digest = HTTPDigestUtil.digest(tokens);

            // Pre-verify the client response
            if (digest.getUsername() == null || digest.getRealm() == null || digest.getNonce() == null
                    || digest.getUri() == null || digest.getClientResponse() == null) {
                return null;
            }

            // Validate Opaque
            if (digest.getOpaque() != null && digest.getOpaque().equals(this.opaque) == false) {
                return null;
            }

            // Validate realm
            if (digest.getRealm().equals(PICKET_BOX_REALM) == false) {
                return null;
            }

            // Validate qop
            if (digest.getQop().equals(this.qop) == false) {
                return null;
            }

            digest.setRequestMethod(request.getMethod());

            // Validate the nonce
            NONCE_VALIDATION_RESULT nonceResult = validateNonce(digest, sessionId);

            if (nonceResult == NONCE_VALIDATION_RESULT.VALID) {
                return authenticationManager.authenticate(digest);
            }
        }        
        
        return null;
    }
    
    private static enum NONCE_VALIDATION_RESULT {
        INVALID, STALE, VALID
    }
    
    protected void challengeClient(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();

        String domain = request.getContextPath();
        if (domain == null)
            domain = "/";

        String newNonce = nonceGenerator.get();

        List<String> storedNonces = idVersusNonce.get(sessionId);
        if (storedNonces == null) {
            storedNonces = new ArrayList<String>();
            idVersusNonce.put(sessionId, storedNonces);
        }
        storedNonces.add(newNonce);

        StringBuilder str = new StringBuilder("Digest realm=\"");
        str.append(PICKET_BOX_REALM).append("\",");
        str.append("domain=\"").append(domain).append("\",");
        str.append("nonce=\"").append(newNonce).append("\",");
        str.append("algorithm=MD5,");
        str.append("qop=").append(this.qop).append(",");
        str.append("opaque=\"").append(this.opaque).append("\",");
        str.append("stale=\"").append(false).append("\"");

        response.setHeader(PicketBoxConstants.HTTP_WWW_AUTHENTICATE, str.toString());

        try {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }
    
    private NONCE_VALIDATION_RESULT validateNonce(DigestHolder digest, String sessionId) {
        String nonce = digest.getNonce();

        List<String> storedNonces = idVersusNonce.get(sessionId);
        if (storedNonces == null) {
            return NONCE_VALIDATION_RESULT.INVALID;
        }
        if (storedNonces.contains(nonce) == false) {
            return NONCE_VALIDATION_RESULT.INVALID;
        }

        boolean hasExpired = nonceGenerator.hasExpired(nonce, nonceMaxValid);
        if (hasExpired)
            return NONCE_VALIDATION_RESULT.STALE;

        return NONCE_VALIDATION_RESULT.VALID;
    }

}
