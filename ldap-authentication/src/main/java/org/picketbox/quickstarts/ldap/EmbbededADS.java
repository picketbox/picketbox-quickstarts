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
import java.io.InputStream;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.apache.directory.server.core.CoreSession;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.entry.DefaultServerEntry;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmIndex;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.server.xdbm.Index;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.apache.directory.shared.ldap.name.LdapDN;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class EmbbededADS {

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
        service.getChangeLog().setEnabled(true);

        // Create a new partition named 'apache'.
        Partition jbossPartition = addPartition("jboss", "dc=jboss,dc=org");

        // Index some attributes on the apache partition
        addIndex(jbossPartition, "objectClass", "ou", "uid");

        File workingDirectory = new File(System.getProperty("java.io.tmpdir") + "/picketbox-embbeded-ldap-server");
        
        if (workingDirectory.exists()) {
            FileUtils.deleteDirectory(workingDirectory);
            workingDirectory.mkdirs();
        }
        
        service.setWorkingDirectory(workingDirectory);

        // And start the service
        service.startup();

        if (!service.getAdminSession().exists(jbossPartition.getSuffixDn())) {
            LdapDN dnJBoss = new LdapDN("dc=jboss,dc=org");
            ServerEntry entryApache = service.newEntry(dnJBoss);
            entryApache.add("objectClass", "top", "domain", "extensibleObject");
            entryApache.add("dc", "apache");
            service.getAdminSession().add(entryApache);
        }

        server = new LdapServer();
        server.setTransports(new TcpTransport(10389));
        server.setDirectoryService(service);
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() {
        server.stop();
    }

    public void importLDIF(String fileName) throws Exception {
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
     * Add a new partition to the server
     * 
     * @param partitionId The partition Id
     * @param partitionDn The partition DN
     * @return The newly added partition
     * @throws Exception If the partition can't be added
     */
    private Partition addPartition(String partitionId, String partitionDn) throws Exception {
        // Create a new partition named 'foo'.
        Partition partition = new JdbmPartition();
        partition.setId(partitionId);
        partition.setSuffix(partitionDn);
        service.addPartition(partition);

        return partition;
    }

    /**
     * Add a new set of index on the given attributes
     * 
     * @param partition The partition on which we want to add index
     * @param attrs The list of attributes to index
     */
    private void addIndex(Partition partition, String... attrs) {
        // Index some attributes on the apache partition
        HashSet<Index<?, ServerEntry>> indexedAttributes = new HashSet<Index<?, ServerEntry>>();

        for (String attribute : attrs) {
            indexedAttributes.add(new JdbmIndex<String, ServerEntry>(attribute));
        }

        ((JdbmPartition) partition).setIndexedAttributes(indexedAttributes);
    }
}
