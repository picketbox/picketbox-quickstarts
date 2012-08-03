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
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Principal;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.authentication.AuthenticationClient;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationProvider;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.AuthenticationService;
import org.picketbox.core.authentication.AuthenticationStatus;
import org.picketbox.core.authentication.manager.PropertiesFileBasedAuthenticationManager;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.test.authentication.spi.sasl.SASLAuthenticationCallbackHandler;
import org.picketbox.core.test.authentication.spi.sasl.TestDIGESTMD5Mechanism;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class SASLAuthenticationProviderTestCase {

    private InputStream serverInputStream;
    private OutputStream serverOutputStream;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private InputStream clientInputStream;
    private OutputStream clientOutputStream;
    
    private PicketBoxManager picketboxManager;

    @Before
    public void onSetup() {
        PicketBoxConfiguration configuration = new PicketBoxConfiguration();

        configuration.authentication().addMechanism(new TestDIGESTMD5Mechanism());
        
        configuration.authentication().addAuthManager(new PropertiesFileBasedAuthenticationManager());
        
        this.picketboxManager = configuration.buildAndStart();
    }

    @Test
    public void testClientAuthentication() throws Exception {

        AuthenticationProvider authenticationProvider = this.picketboxManager.getAuthenticationProvider();

        final AuthenticationMechanism mechanism = authenticationProvider.getMechanism(TestDIGESTMD5Mechanism.class.getName());

        new Thread(new Runnable() {
            public void run() {
                initializeServer();
                final AuthenticationService service = mechanism.getService();

                try {
                    AuthenticationResult result = null;
                    AuthenticationStatus status = AuthenticationStatus.CONTINUE;

                    while (status.equals(AuthenticationStatus.CONTINUE)) {
                        result = service.authenticate(new SASLAuthenticationCallbackHandler(
                                serverInputStream, serverOutputStream, "TestProtocol", "TestServer",
                                "TestRealm"));
                        status = result.getStatus();
                    }
                    
                    if (result.getStatus().equals(AuthenticationStatus.SUCCESS)) {
                        // user is authenticated
                        Principal subject = result.getPrincipal();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail("Server authentication failed.");
                }
            }
        }) {
        }.start();

        final AuthenticationClient client = mechanism.getClient();

        initializeClient();

        AuthenticationResult result = client.authenticate(new SASLAuthenticationCallbackHandler(clientInputStream, clientOutputStream, "admin", "admin",
                "admin", "TestProtocol", "TestServer", "TestRealm"));

        Assert.assertEquals(AuthenticationStatus.SUCCESS, result.getStatus());
        
        Thread.sleep(10000);
        
        this.serverSocket.close();
        this.clientSocket.close();
    }

    public void initializeServer() {
        try {
            this.serverSocket = new ServerSocket(2401);

            Socket socket = serverSocket.accept();

            this.serverInputStream = socket.getInputStream();
            this.serverOutputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void initializeClient() {
        try {
            this.clientSocket = new Socket("localhost", 2401);

            this.clientInputStream = clientSocket.getInputStream();
            this.clientOutputStream = clientSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
