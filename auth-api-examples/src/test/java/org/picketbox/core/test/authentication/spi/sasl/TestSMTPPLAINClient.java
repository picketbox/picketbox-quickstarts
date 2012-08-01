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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

import org.picketbox.core.authentication.AuthenticationCallbackHandler;
import org.picketbox.core.authentication.AuthenticationInfo;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.AuthenticationStatus;
import org.picketbox.core.authentication.impl.AbstractAuthenticationClient;

import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64EncoderStream;

public class TestSMTPPLAINClient extends AbstractAuthenticationClient {

    private String host;
    
    public TestSMTPPLAINClient() {
        
    }
    
    public TestSMTPPLAINClient(String host) {
        this.host = host;
    }

    public List<AuthenticationInfo> getAuthenticationInfo() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.authentication.api.AuthenticationClient#authenticate(org.picketbox.core.authentication.api.AuthenticationCallbackHandler)
     */
    public AuthenticationResult authenticate(AuthenticationCallbackHandler handler) {
        AuthenticationResult result = new AuthenticationResult();
        
        NameCallback nameCallback = new NameCallback("User name:");
        PasswordCallback passwordCallback = new PasswordCallback("Password:", false);
        SMTPTransportCallback transportCallback = new SMTPTransportCallback(); 
        
        try {
            handler.handle(new Callback[] {nameCallback, passwordCallback, transportCallback});
        } catch (Exception e) {
            return authenticationFailed(result);
        }

        SMTPTransport transport = transportCallback.getTransport();
        
        SaslClient sc = null;
        
        try {
            sc = Sasl.createSaslClient(new String[] {"PLAIN"}, nameCallback.getName(), "smtp", host,
                                        (Map) Collections.emptyMap(), handler);
        } catch (SaslException sex) {
            return authenticationFailed(result);
        }
        
        if (sc == null) {
            return authenticationFailed(result);
        }

        int resp = 0;
        try {
            String mech = sc.getMechanismName();
            String ir = null;
            if (sc.hasInitialResponse()) {
                byte[] ba = sc.evaluateChallenge(new byte[0]);
                ba = BASE64EncoderStream.encode(ba);
                ir = ASCIIUtility.toString(ba, 0, ba.length);
            }
            if (ir != null)
                resp = transport.simpleCommand("AUTH " + mech + " " + ir);
            else
                resp = transport.simpleCommand("AUTH " + mech);

            if (resp == 530) {
                if (ir != null)
                    resp = transport.simpleCommand("AUTH " + mech + " " + ir);
                else
                    resp = transport.simpleCommand("AUTH " + mech);
            }
            
            if (resp == 235)
                return performSuccessfulAuthentication(result);

            if (resp != 334)
                return authenticationFailed(result);
        } catch (Exception ex) {
            return authenticationFailed(result);
        }

        return authenticationFailed(result);
    }

}
