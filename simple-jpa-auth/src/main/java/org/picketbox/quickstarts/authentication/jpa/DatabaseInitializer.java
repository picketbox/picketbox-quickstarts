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

package org.picketbox.quickstarts.authentication.jpa;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.solder.servlet.event.Destroyed;
import org.jboss.solder.servlet.event.Initialized;

/**
 * <p>Initializes the database with the users table and loads a default user data.</p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
@ApplicationScoped
public class DatabaseInitializer {

    private EntityManagerFactory factory;

    public void contextInitialized(@Observes @Initialized ServletContext servletContext) {
        try {
            this.factory = (EntityManagerFactory) new InitialContext().lookup("java:/AuthTestEMF");
            EntityManager entityManager = factory.createEntityManager();
            
            entityManager.getTransaction().begin();
            
            Query dropUserTableStm = entityManager.createNativeQuery("DROP TABLE IF EXISTS PICKETBOX_USERS;");
            Query createUserTableStm = entityManager.createNativeQuery("CREATE TABLE PICKETBOX_USERS(username varchar2(20) not null, password varchar2(20) not null);");
            Query loadUserDataStm = entityManager.createNativeQuery("INSERT INTO PICKETBOX_USERS(username, password) VALUES ('admin', 'admin');");
            
            dropUserTableStm.executeUpdate();
            createUserTableStm.executeUpdate();
            loadUserDataStm.executeUpdate();

            entityManager.getTransaction().commit();
            
            entityManager.close();
        } catch (NamingException e) {
            throw new IllegalStateException("Error initializing database.", e);
        }
    }
    
    public void contextDestroyed(@Observes @Destroyed ServletContext servletContext) {
        this.factory.close();
    }

}
