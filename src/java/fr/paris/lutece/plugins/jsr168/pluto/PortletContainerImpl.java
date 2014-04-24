/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */

/*
 * Copyright 2003,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.paris.lutece.plugins.jsr168.pluto;

import fr.paris.lutece.plugins.jsr168.pluto.core.PortletURLProviderImpl;

import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerServices;
import org.apache.pluto.core.InternalActionResponse;
import org.apache.pluto.factory.PortletObjectAccess;
import org.apache.pluto.invoker.PortletInvoker;
import org.apache.pluto.invoker.PortletInvokerAccess;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.portalImpl.om.window.impl.PortletWindowImpl;
import org.apache.pluto.services.PortletContainerEnvironment;
import org.apache.pluto.services.information.InformationProviderAccess;
import org.apache.pluto.services.log.LogService;
import org.apache.pluto.services.log.Logger;

import java.io.IOException;

import java.util.Map;
import java.util.Properties;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


/**
 * Portlet Pluto container.<b>
 *
 * Pluto's uses of render parameters was rebuild.
 *
 * Based on Apache Pluto Container <code>org.apache.pluto.PortletContainerImpl</code> class.
 */
public class PortletContainerImpl implements PortletContainer
{
    private static String CONTAINER_SUPPORTS_BUFFERING = "portletcontainer.supportsBuffering";
    private String uniqueContainerName;
    private boolean initialized;
    private boolean supportsBuffering;
    private Logger log = null;

    /**
         * @see org.apache.pluto.PortletContainer#init(java.lang.String, javax.servlet.ServletConfig, org.apache.pluto.services.PortletContainerEnvironment, java.util.Properties)
         */
    public void init( String uniqueContainerName, ServletConfig servletConfig, PortletContainerEnvironment environment,
        Properties properties )
    {
        this.uniqueContainerName = uniqueContainerName;
        PortletContainerServices.createReference( uniqueContainerName, environment );
        initialized = true;

        // Initialize the Logger that we will use
        // from here forward for this Container:
        log = ( (LogService) environment.getContainerService( LogService.class ) ).getLogger( getClass(  ) );

        Boolean b = (Boolean) properties.get( CONTAINER_SUPPORTS_BUFFERING );

        if ( b == null )
        {
            log.warn( "org.apache.pluto.PortletContainerImpl#init(): " + "Couldn't retrieve parameter \"" +
                CONTAINER_SUPPORTS_BUFFERING + "\" from" +
                "passed properties object. Falling back to default value \"FALSE\"" );
            supportsBuffering = false;
        }
        else
        {
            supportsBuffering = b.booleanValue(  );
        }
    }

    /**
         * @see org.apache.pluto.PortletContainer#shutdown()
         */
    public void shutdown(  )
    {
        PortletContainerServices.destroyReference( uniqueContainerName );
    }

    /**
         * @see org.apache.pluto.PortletContainer#renderPortlet(org.apache.pluto.om.window.PortletWindow, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
         */
    public void renderPortlet( PortletWindow portletWindow, HttpServletRequest servletRequest,
        HttpServletResponse servletResponse ) throws PortletException, IOException
    {
        PortletContainerServices.prepare( uniqueContainerName );

        PortletInvoker invoker = null;

        if ( log.isDebugEnabled(  ) )
        {
            log.debug( "PortletContainerImpl.portletService(" + portletWindow.getId(  ) + ") called." );
        }

        try
        {
            RenderRequest renderRequest = PortletObjectAccess.getRenderRequest( portletWindow, servletRequest,
                    servletResponse );

            RenderResponse renderResponse = PortletObjectAccess.getRenderResponse( portletWindow, servletRequest,
                    servletResponse, supportsBuffering );

            invoker = PortletInvokerAccess.getPortletInvoker( portletWindow.getPortletEntity(  ).getPortletDefinition(  ) );
            invoker.render( renderRequest, renderResponse );

            ( (PortletWindowImpl) portletWindow ).saveValues(  );
        }
        catch ( PortletException e )
        {
            ( (PortletWindowImpl) portletWindow ).restoreValues(  );
            throw e;
        }
        catch ( IOException e )
        {
            ( (PortletWindowImpl) portletWindow ).restoreValues(  );
            throw e;
        }
        catch ( RuntimeException e )
        {
            ( (PortletWindowImpl) portletWindow ).restoreValues(  );
            throw e;
        }
        finally
        {
            PortletInvokerAccess.releasePortletInvoker( invoker );
            PortletContainerServices.release(  );
        }
    }

    /**
         * @see org.apache.pluto.PortletContainer#processPortletAction(org.apache.pluto.om.window.PortletWindow, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
         */
    public void processPortletAction( final PortletWindow portletWindow, final HttpServletRequest servletRequest,
        final HttpServletResponse servletResponse ) throws PortletException, IOException
    {
        PortletContainerServices.prepare( uniqueContainerName );

        PortletInvoker invoker = null;

        if ( log.isDebugEnabled(  ) )
        {
            log.debug( "PortletContainerImpl.performPortletAction(" + portletWindow.getId(  ) + ") called." );
        }

        InternalActionResponse innerActionResponse = null;
        ActionRequest actionRequest = null;

        try
        {
            /*ActionRequest*/
            actionRequest = PortletObjectAccess.getActionRequest( portletWindow, servletRequest, servletResponse );

            ActionResponse actionResponse = PortletObjectAccess.getActionResponse( portletWindow, servletRequest,
                    servletResponse );

            invoker = PortletInvokerAccess.getPortletInvoker( portletWindow.getPortletEntity(  ).getPortletDefinition(  ) );
            innerActionResponse = (InternalActionResponse) actionResponse;
            // call action() at the portlet
            invoker.action( actionRequest, actionResponse );

            String location = innerActionResponse.getRedirectLocation(  );

            // Action ask for a redirect... 
            if ( location != null )
            {
                sendRedirect( servletResponse, location );
            }
            else
            {
                ( (PortletWindowImpl) portletWindow ).saveValues(  );

                // get the changings of this portlet entity that might be set during action handling
                // change portlet mode
                if ( innerActionResponse.getChangedPortletMode(  ) != null )
                {
                    InformationProviderAccess.getDynamicProvider( servletRequest )
                                             .getPortletActionProvider( portletWindow )
                                             .changePortletMode( innerActionResponse.getChangedPortletMode(  ) );
                }

                // change window state
                if ( innerActionResponse.getChangedWindowState(  ) != null )
                {
                    InformationProviderAccess.getDynamicProvider( servletRequest )
                                             .getPortletActionProvider( portletWindow )
                                             .changePortletWindowState( innerActionResponse.getChangedWindowState(  ) );
                }

                // get render parameters
                Map renderParameter = innerActionResponse.getRenderParameters(  );
                log.debug( "fix params of " + portletWindow.getId(  ) + " to " + renderParameter );
                ( (PortletWindowImpl) portletWindow ).setRenderParameter( renderParameter );
            }
        }
        catch ( PortletException e )
        {
            ( (PortletWindowImpl) portletWindow ).restoreValues(  );
            throw e;
        }
        catch ( IOException e )
        {
            ( (PortletWindowImpl) portletWindow ).restoreValues(  );
            throw e;
        }
        catch ( RuntimeException e )
        {
            ( (PortletWindowImpl) portletWindow ).restoreValues(  );
            throw e;
        }
        finally
        {
            try
            {
                String strLocation = PortletURLProviderImpl.getRedirectPortalURL( servletRequest, servletResponse );
                sendRedirect( servletResponse, strLocation );
            }
            finally
            {
                PortletInvokerAccess.releasePortletInvoker( invoker );
                PortletContainerServices.release(  );
            }
        }
    }

    /**
         * @see org.apache.pluto.PortletContainer#portletLoad(org.apache.pluto.om.window.PortletWindow, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
         */
    public void portletLoad( PortletWindow portletWindow, HttpServletRequest servletRequest,
        HttpServletResponse servletResponse ) throws PortletException
    {
        PortletContainerServices.prepare( uniqueContainerName );

        PortletInvoker invoker = null;

        if ( log.isDebugEnabled(  ) )
        {
            log.debug( "PortletContainerImpl.portletLoad(" + portletWindow.getId(  ) + ") called." );
        }

        RenderRequest renderRequest = PortletObjectAccess.getRenderRequest( portletWindow, servletRequest,
                servletResponse );

        RenderResponse renderResponse = PortletObjectAccess.getRenderResponse( portletWindow, servletRequest,
                servletResponse, supportsBuffering );

        invoker = PortletInvokerAccess.getPortletInvoker( portletWindow.getPortletEntity(  ).getPortletDefinition(  ) );

        try
        {
            invoker.load( renderRequest, renderResponse );
        }
        finally
        {
            PortletInvokerAccess.releasePortletInvoker( invoker );
            PortletContainerServices.release(  );
        }
    }

    /**
         * @see org.apache.pluto.PortletContainer#isInitialized()
         */
    public boolean isInitialized(  )
    {
        return initialized;
    }

    /**
     * Send a redirect response to user, use true deeply defined HttpServletResponse,
     * because response wrapped for portlets can't send redirect).
     *
         * @param servletResponse The current HTTP response
         * @param location URL location to send
         * @throws IOException for IO exceptions will sending redirect
         */
    private void sendRedirect( HttpServletResponse servletResponse, String location )
        throws IOException
    {
        HttpServletResponse redirectResponse = servletResponse;

        while ( redirectResponse instanceof HttpServletResponseWrapper )
        {
            redirectResponse = (HttpServletResponse) ( (HttpServletResponseWrapper) redirectResponse ).getResponse(  );
        }

        redirectResponse.sendRedirect( location );
    }
}
