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

package org.picketbox.quickstarts.cdi.exception;

import java.io.IOException;
import java.util.Iterator;

import javax.enterprise.inject.spi.BeanManager;
import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;

import org.apache.deltaspike.core.api.exception.control.event.ExceptionToCatchEvent;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;

/**
 * <p>
 * {@link ExceptionHandler} implementation that fires {@link ExceptionToCatchEvent} events to be handled by the application
 * exception handlers marked with the {@link org.apache.deltaspike.core.api.exception.control.annotation.ExceptionHandler}
 * annotation.
 * </p>
 * 
 * @author Pedro Silva
 * 
 */
public class DeltaSpikeExceptionHandler extends ExceptionHandlerWrapper {

    private BeanManager beanManager;

    private ExceptionHandler wrapped;

    public DeltaSpikeExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
        this.beanManager = BeanManagerProvider.getInstance().getBeanManager();
    }

    @Override
    public ExceptionHandler getWrapped() {
        return this.wrapped;
    }

    @Override
    public void handle() throws FacesException {
        Iterator<ExceptionQueuedEvent> it = getUnhandledExceptionQueuedEvents().iterator();

        if (it.hasNext()) {
            while (it.hasNext()) {
                try {
                    ExceptionQueuedEvent evt = it.next();

                    Throwable exception = evt.getContext().getException();

                    ExceptionToCatchEvent etce = new ExceptionToCatchEvent(exception);

                    beanManager.fireEvent(etce);
                } finally {
                    it.remove();
                }
            }

            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();

            try {
                externalContext.redirect(externalContext.getRequestContextPath() + "/error.jsf");
            } catch (IOException e) {
            }

            facesContext.responseComplete();
        }

        getWrapped().handle();
    }
}
