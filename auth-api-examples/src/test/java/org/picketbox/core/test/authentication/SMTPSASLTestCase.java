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

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.authentication.AuthenticationClient;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationProvider;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.AuthenticationStatus;
import org.picketbox.core.authentication.manager.PropertiesFileBasedAuthenticationManager;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.test.authentication.spi.sasl.TestDIGESTMD5Mechanism;
import org.picketbox.core.test.authentication.spi.smtp.SMTPAuthenticationCallbackHandler;
import org.picketbox.core.test.authentication.spi.smtp.TestSMTPPLAINMechanism;

import com.sun.mail.smtp.SMTPTransport;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class SMTPSASLTestCase {

    private PicketBoxManager picketboxManager;

    @Before
    public void onSetup() {
        PicketBoxConfiguration configuration = new PicketBoxConfiguration();

        configuration.authentication().addMechanism(new TestSMTPPLAINMechanism());
        
        configuration.authentication().addAuthManager(new PropertiesFileBasedAuthenticationManager());
        
        this.picketboxManager = configuration.buildAndStart();
    }
    
    @Test
    public void testAuthenticate() throws Exception {

        AuthenticationProvider authenticationProvider = picketboxManager.getAuthenticationProvider();

        final AuthenticationMechanism mechanism = authenticationProvider.getMechanism(TestSMTPPLAINMechanism.class.getName());
        
        String host = "smtp.gmail.com";
        
        AuthenticationClient client = mechanism.getClient();

        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.starttls.enable", "true");
        
        Session session = Session.getInstance(props);
        
        session.setDebug(true);
        
        SMTPTransport transport = null;
        
        try {
            transport = (SMTPTransport) session.getTransport("smtp");
        } catch (NoSuchProviderException e1) {
            e1.printStackTrace();
        }
        
        try {
            transport.connect(host, null, null);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        
        AuthenticationResult status = client.authenticate(new SMTPAuthenticationCallbackHandler("user@gmail.com", "password", transport));
        
        Assert.assertEquals(AuthenticationStatus.SUCCESS, status.getStatus());
    }
    
}
