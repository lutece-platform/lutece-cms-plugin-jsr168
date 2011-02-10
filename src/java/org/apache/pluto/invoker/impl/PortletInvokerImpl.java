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

/*

 */
package org.apache.pluto.invoker.impl;

import org.apache.pluto.Constants;
import org.apache.pluto.PortletContainerServices;
import org.apache.pluto.core.CoreUtils;
import org.apache.pluto.core.InternalPortletRequest;
import org.apache.pluto.core.InternalPortletResponse;
import org.apache.pluto.factory.PortletObjectAccess;
import org.apache.pluto.invoker.PortletInvoker;
import org.apache.pluto.om.ControllerObjectAccess;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionCtrl;
import org.apache.pluto.om.servlet.ServletDefinition;
import org.apache.pluto.services.log.LogService;
import org.apache.pluto.services.log.Logger;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


public class PortletInvokerImpl implements PortletInvoker
{
    // XXX LUTECE: pour se passer de PortletServlet!	
    static private Map _portletsStock = new HashMap(  );
    static private Map _portletContextsStock = new HashMap(  );
    static private Map _portletConfigsStock = new HashMap(  );
    private ServletConfig servletConfig;
    private PortletDefinition portletDefinition;

    /* This Logger can be saved due to the
     * fact that a unique instance of PortletInvoker
     * will be used for each request. We load it
     * lazily since we only log exceptions at
     * this point.
     */
    private Logger log = null;

    public PortletInvokerImpl( PortletDefinition portletDefinition, javax.servlet.ServletConfig servletConfig )
    {
        this.portletDefinition = portletDefinition;
        this.servletConfig = servletConfig;
    }

    // org.apache.pluto.invoker.PortletInvoker implementation -------------------------------------
    public void action( ActionRequest request, ActionResponse response )
        throws PortletException, IOException
    {
        invoke( request, response, Constants.METHOD_ACTION );
    }

    public void render( RenderRequest request, RenderResponse response )
        throws PortletException, IOException
    {
        invoke( request, response, Constants.METHOD_RENDER );
    }

    public void load( PortletRequest request, RenderResponse response )
        throws PortletException
    {
        try
        {
            invoke( request, response, Constants.METHOD_NOOP );
        }
        catch ( IOException e )
        {
            getLog(  ).error( "PortletInvokerImpl.load() - Error while dispatching portlet.", e );
            throw new PortletException( e );
        }
    }

    // --------------------------------------------------------------------------------------------

    // additional methods -------------------------------------------------------------------------
    /*
     * generic method to be used called by both, action and render
     */
    protected void invoke( PortletRequest portletRequest, PortletResponse portletResponse, Integer methodID )
        throws PortletException, IOException
    {
        InternalPortletRequest internalPortletRequest = CoreUtils.getInternalRequest( portletRequest );

        InternalPortletResponse internalPortletResponse = CoreUtils.getInternalResponse( portletResponse );

        // gather all required data from request and response
        ServletRequest servletRequest = ( (HttpServletRequestWrapper) internalPortletRequest ).getRequest(  );

        ServletResponse servletResponse = ( (HttpServletResponseWrapper) internalPortletResponse ).getResponse(  );

        ServletDefinition servletDefinition = portletDefinition.getServletDefinition(  );
        ServletContext servletContext = servletConfig.getServletContext(  );

        Portlet portlet;
        PortletContext portletContext = null;
        PortletConfig portletConfig = null;

        synchronized ( this )
        {
            portlet = (Portlet) _portletsStock.get( portletDefinition.getId(  ) );

            if ( portlet == null )
            {
                try
                {
                    portlet = (Portlet) Thread.currentThread(  ).getContextClassLoader(  )
                                              .loadClass( portletDefinition.getClassName(  ) ).newInstance(  );
                    _portletsStock.put( portletDefinition.getId(  ), portlet );
                }
                catch ( ClassNotFoundException e )
                {
                    throw new PortletException( e );
                }
                catch ( IllegalAccessException e )
                {
                    throw new PortletException( e );
                }
                catch ( InstantiationException e )
                {
                    throw new PortletException( e );
                }
            }
            else
            {
                portletContext = (PortletContext) _portletContextsStock.get( portletDefinition.getId(  ) );
                portletConfig = (PortletConfig) _portletConfigsStock.get( portletDefinition.getId(  ) );
            }
        }

        if ( portletContext == null )
        {
            synchronized ( portlet )
            {
                portletContext = (PortletContext) _portletContextsStock.get( portletDefinition.getId(  ) );
                portletConfig = (PortletConfig) _portletConfigsStock.get( portletDefinition.getId(  ) );

                if ( portletContext == null )
                {
                    PortletDefinitionCtrl portletDefCtrl = (PortletDefinitionCtrl) ControllerObjectAccess.get( portletDefinition );
                    portletDefCtrl.setPortletClassLoader( Thread.currentThread(  ).getContextClassLoader(  ) );

                    portletContext = PortletObjectAccess.getPortletContext( servletConfig.getServletContext(  ),
                            portletDefinition.getPortletApplicationDefinition(  ) );
                    portletConfig = PortletObjectAccess.getPortletConfig( servletConfig, portletContext,
                            portletDefinition );

                    portlet.init( portletConfig );

                    _portletContextsStock.put( portletDefinition.getId(  ), portletContext );
                    _portletConfigsStock.put( portletDefinition.getId(  ), portletConfig );
                }
            }
        }

        try
        {
            servletRequest.setAttribute( "javax.portlet.config", portletConfig );

            if ( Constants.METHOD_ACTION.equals( methodID ) )
            {
                // prepare container objects to run in this webModule
                prepareActionRequest( (ActionRequest) internalPortletRequest, (HttpServletRequest) servletRequest );
                prepareActionResponse( (ActionResponse) internalPortletResponse, (HttpServletRequest) servletRequest,
                    (HttpServletResponse) servletResponse );

                portletRequest.setAttribute( Constants.PORTLET_REQUEST, portletRequest );
                portletRequest.setAttribute( Constants.PORTLET_RESPONSE, portletResponse );

                portlet.processAction( (ActionRequest) portletRequest, (ActionResponse) portletResponse );
            }
            else if ( Constants.METHOD_RENDER.equals( methodID ) )
            {
                // prepare container objects to run in this webModule
                prepareRenderRequest( (RenderRequest) internalPortletRequest, (HttpServletRequest) servletRequest );
                prepareRenderResponse( (RenderResponse) internalPortletResponse, (HttpServletRequest) servletRequest,
                    (HttpServletResponse) servletResponse );

                portletRequest.setAttribute( Constants.PORTLET_REQUEST, portletRequest );
                portletRequest.setAttribute( Constants.PORTLET_RESPONSE, portletResponse );

                portlet.render( (RenderRequest) portletRequest, (RenderResponse) portletResponse );
            }
            else if ( Constants.METHOD_NOOP.equals( methodID ) )
            {
                // RIEN A FAIRE
            }
        }
        finally
        {
            servletRequest.removeAttribute( "javax.portlet.config" );
        }
    }

    // --------------------------------------------------------------------------------------------
    private void prepareActionRequest( ActionRequest portletRequest, HttpServletRequest servletRequest )
    {
        InternalPortletRequest internalPortletRequest = CoreUtils.getInternalRequest( portletRequest );

        internalPortletRequest.lateInit( servletRequest );
    }

    private void prepareRenderRequest( RenderRequest portletRequest, HttpServletRequest servletRequest )
    {
        InternalPortletRequest internalPortletRequest = CoreUtils.getInternalRequest( portletRequest );

        internalPortletRequest.lateInit( servletRequest );
    }

    private void prepareRenderResponse( RenderResponse portletResponse, HttpServletRequest servletRequest,
        HttpServletResponse servletResponse )
    {
        InternalPortletResponse internalPortletResponse = CoreUtils.getInternalResponse( portletResponse );

        internalPortletResponse.lateInit( servletRequest, servletResponse );
    }

    private void prepareActionResponse( ActionResponse portletResponse, HttpServletRequest servletRequest,
        HttpServletResponse servletResponse )
    {
        InternalPortletResponse internalPortletResponse = CoreUtils.getInternalResponse( portletResponse );

        internalPortletResponse.lateInit( servletRequest, servletResponse );
    }

    /** Provides lazy instantiation of the Logger.
    *  This is usefull since the log is currently only
    *  used when an error occurs.  B/C of this, there is
    *  no reason to retrieve the log until needed.
    * @return
    */
    private Logger getLog(  )
    {
        if ( log == null )
        {
            // from here forward for this Container:
            log = ( (LogService) PortletContainerServices.get( LogService.class ) ).getLogger( getClass(  ) );
        }

        return log;
    }
}
