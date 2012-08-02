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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationProvider;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.AuthenticationService;
import org.picketbox.core.authentication.AuthenticationStatus;
import org.picketbox.core.authentication.DigestHolder;
import org.picketbox.core.authentication.PicketBoxConstants;
import org.picketbox.core.authentication.manager.PropertiesFileBasedAuthenticationManager;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.exceptions.FormatException;
import org.picketbox.core.test.authentication.spi.http.HTTPDigestAuthHandler;
import org.picketbox.core.test.authentication.spi.http.HTTPDigestMechanism;
import org.picketbox.core.util.HTTPDigestUtil;
import org.picketbox.http.PicketBoxManager;
import org.picketbox.http.config.PicketBoxConfiguration;
import org.picketbox.test.http.TestServletRequest;
import org.picketbox.test.http.TestServletResponse;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class HTTPDigestAuthenticationProviderTestCase {

    private PicketBoxManager picketboxManager;

    @Before
    public void onSetup() {
        PicketBoxConfiguration configuration = new PicketBoxConfiguration();

        configuration.authentication().addMechanism(new HTTPDigestMechanism());
        
        configuration.authentication().addAuthManager(new PropertiesFileBasedAuthenticationManager());
        
        this.picketboxManager = (PicketBoxManager) configuration.buildAndStart();
    }
    
    @Test
    public void testMechanismConfiguration() throws AuthenticationException {
        TestServletRequest req = new TestServletRequest(new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        });

        TestServletResponse resp = new TestServletResponse(new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                System.out.println(b);
            }
        });

        req.setMethod("GET");

        AuthenticationProvider authenticationProvider = this.picketboxManager.getAuthenticationProvider();
        AuthenticationMechanism authenticationMechanism = authenticationProvider.getMechanism(HTTPDigestMechanism.class.getName());
        AuthenticationService authenticationService = authenticationMechanism.getService();
        
        // first request
        AuthenticationResult result = authenticationService.authenticate(new HTTPDigestAuthHandler(req, resp));
        
        Assert.assertEquals(AuthenticationStatus.CONTINUE, result.getStatus());

        String authorizationHeader = resp.getHeader(PicketBoxConstants.HTTP_WWW_AUTHENTICATE);
        authorizationHeader = authorizationHeader.substring(7);
        String[] tokens = HTTPDigestUtil.quoteTokenize(authorizationHeader);

        // Let us get the digest info
        DigestHolder digest = HTTPDigestUtil.digest(tokens);

        // Get Positive Authentication
        req.addHeader(PicketBoxConstants.HTTP_AUTHORIZATION_HEADER, "Digest " + getPositive(digest));

        result = authenticationService.authenticate(new HTTPDigestAuthHandler(req, resp));
        
        Assert.assertEquals(AuthenticationStatus.SUCCESS, result.getStatus());
        Assert.assertNotNull(result.getSubject().getUser());
    }
    
    private String getPositive(DigestHolder digest) {
        String cnonce = "0a4f113b";
        String clientResponse = null;
        try {
            digest.setUsername("Mufasa");
            digest.setRequestMethod("GET");
            digest.setUri("/dir/index.html");
            digest.setCnonce(cnonce);
            digest.setNc("00000001");
            digest.setQop("auth");

            clientResponse = HTTPDigestUtil.clientResponseValue(digest, "Circle Of Life".toCharArray());
        } catch (FormatException e) {
            throw new RuntimeException(e);
        }

        StringBuilder str = new StringBuilder(" username=\"Mufasa\",");

        str.append("realm=\"" + digest.getRealm() + "\",");
        str.append("nonce=\"").append(digest.getNonce()).append("\",");
        str.append("uri=\"/dir/index.html\",");
        str.append("qop=auth,").append("nc=00000001,").append("cnonce=\"" + cnonce + "\",");
        str.append("response=\"" + clientResponse + "\",");
        str.append("opaque=\"").append(digest.getOpaque()).append("\"");
        return str.toString();
    }

}
