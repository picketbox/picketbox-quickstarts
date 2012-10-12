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

package org.picketbox.quickstarts.ldap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.directory.server.core.CoreSession;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.entry.DefaultServerEntry;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.ldif.LdifReader;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class EmbbededADS {

    private static final String DEFAULT_WORKING_DIR = System.getProperty("java.io.tmpdir") + "/picketbox-embbeded-ldap-server";
    private DefaultDirectoryService service;
    private LdapServer server;

    public EmbbededADS() throws Exception {
        init();
    }

    /**
     * Initialize the server. It creates the partition, adds the index, and injects the context entries for the created
     * partitions.
     * 
     * @throws Exception if there were some problems while initializing the system
     */
    private void init() throws Exception {
        // Initialize the LDAP service
        service = new DefaultDirectoryService();

        // Disable the ChangeLog system
        service.getChangeLog().setEnabled(false);

        // Create the default partition
        createDefaultPartition();

        initializeWorkingDir();
        
        // And start the service
        startServer();
        
        // import default data 
        importLDIF("ldap/users.ldif");
    }

    private void startServer() throws Exception {
        service.startup();
        server = new LdapServer();
        server.setTransports(new TcpTransport(10389));
        server.setDirectoryService(service);
    }

    private void initializeWorkingDir() throws IOException {
        File workingDirectory = new File(DEFAULT_WORKING_DIR);
        
        if (workingDirectory.exists()) {
            FileUtils.deleteDirectory(workingDirectory);
            workingDirectory.mkdirs();
        }
        
        service.setWorkingDirectory(workingDirectory);
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() {
        if (server.isStarted()) {
            server.stop();            
        }
    }

    private void importLDIF(String fileName) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        CoreSession rootDSE = service.getAdminSession();
        
        for (LdifEntry ldifEntry : new LdifReader(is)) {
            DefaultServerEntry entry = new DefaultServerEntry(rootDSE.getDirectoryService().getRegistries(),
                    ldifEntry.getEntry());

            if (!rootDSE.exists(entry.getDn())) {
                rootDSE.add(entry);
            }
        }
    }

    /**
     * Add a default partition to the server
     * 
     * @param partitionId The partition Id
     * @param partitionDn The partition DN
     * @return The newly added partition
     * @throws Exception If the partition can't be added
     */
    private void createDefaultPartition() throws Exception {
        Partition partition = new JdbmPartition();
        partition.setId("jboss");
        partition.setSuffix("dc=jboss,dc=org");
        
        service.addPartition(partition);
    }

}
