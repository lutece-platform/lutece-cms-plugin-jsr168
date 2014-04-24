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
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Pluto", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package fr.paris.lutece.plugins.jsr168.pluto.core;

import fr.paris.lutece.plugins.jsr168.pluto.LutecePlutoConstant;

import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.portalImpl.om.window.impl.PortletWindowImpl;
import org.apache.pluto.portalImpl.services.config.Config;
import org.apache.pluto.services.information.DynamicInformationProvider;
import org.apache.pluto.services.information.PortletActionProvider;
import org.apache.pluto.services.information.PortletURLProvider;
import org.apache.pluto.services.information.ResourceURLProvider;

import java.util.HashSet;
import java.util.Iterator;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import javax.servlet.http.HttpServletRequest;


/**
 * Lutece/Pluto implementation of {@link org.apache.pluto.services.information.DynamicInformationProvider}
 *
 * @see fr.paris.lutece.plugins.jsr168.pluto.core.InformationProviderServiceFactoryImpl
 */
public class DynamicInformationProviderImpl implements DynamicInformationProvider
{
    private static final int NumberOfKnownMimetypes = 15;
    private final HttpServletRequest _request;
    private PortalEnvironment _env;

    /**
     * Initialize the new instance (extract {@link PortalEnvironment} from
     * <code>request</code>)
     *
         * @param request The current HTTP request
         */
    DynamicInformationProviderImpl( HttpServletRequest request )
    {
        _request = request;
        _env = PortalEnvironment.getPortalEnvironment( request );
    }

    /**
     * @see org.apache.pluto.services.information.DynamicInformationProvider#getPortletMode(org.apache.pluto.om.window.PortletWindow)
     */
    public PortletMode getPortletMode( PortletWindow portletWindow )
    {
        return ( (PortletWindowImpl) portletWindow ).getPortletMode(  );
    }

    /**
     * @see org.apache.pluto.services.information.DynamicInformationProvider#getPortletURLProvider(org.apache.pluto.om.window.PortletWindow)
     */
    public PortletURLProvider getPortletURLProvider( PortletWindow portletWindow )
    {
        return new PortletURLProviderImpl( _request, this, portletWindow );
    }

    /**
     * @see org.apache.pluto.services.information.DynamicInformationProvider#getResourceURLProvider(org.apache.pluto.om.window.PortletWindow)
     */
    public ResourceURLProvider getResourceURLProvider( PortletWindow portletWindow )
    {
        return new ResourceURLProviderImpl( this, portletWindow );
    }

    /**
     * @see org.apache.pluto.services.information.DynamicInformationProvider#getPortletActionProvider(org.apache.pluto.om.window.PortletWindow)
     */
    public PortletActionProvider getPortletActionProvider( PortletWindow portletWindow )
    {
        return new PortletActionProviderImpl( (PortletWindowImpl) portletWindow );
    }

    /**
     * @see org.apache.pluto.services.information.DynamicInformationProvider#getPreviousPortletMode(org.apache.pluto.om.window.PortletWindow)
     */
    public PortletMode getPreviousPortletMode( PortletWindow portletWindow )
    {
        return ( (PortletWindowImpl) portletWindow ).getPrevPortletMode(  );
    }

    /**
     * @see org.apache.pluto.services.information.DynamicInformationProvider#getPreviousWindowState(org.apache.pluto.om.window.PortletWindow)
     */
    public WindowState getPreviousWindowState( PortletWindow portletWindow )
    {
        return ( (PortletWindowImpl) portletWindow ).getPrevWindowState(  );
    }

    /**
     * @see org.apache.pluto.services.information.DynamicInformationProvider#getResponseContentType()
     */
    public String getResponseContentType(  )
    {
        return "text/html";
    }

    /**
     * @see org.apache.pluto.services.information.DynamicInformationProvider#getResponseContentTypes()
     */
    public Iterator getResponseContentTypes(  )
    {
        HashSet responseMimeTypes = new HashSet( NumberOfKnownMimetypes );
        responseMimeTypes.add( "text/html" );

        return responseMimeTypes.iterator(  );
    }

    /**
     * @see org.apache.pluto.services.information.DynamicInformationProvider#getWindowState(org.apache.pluto.om.window.PortletWindow)
     */
    public WindowState getWindowState( final PortletWindow portletWindow )
    {
        return ( (PortletWindowImpl) portletWindow ).getWindowState(  );
    }

    /**
     * @see org.apache.pluto.services.information.DynamicInformationProvider#isPortletModeAllowed(javax.portlet.PortletMode)
     */
    public boolean isPortletModeAllowed( PortletMode mode )
    {
        //checks whether PortletMode is supported as example
        String[] supportedModes = Config.getParameters(  )
                                        .getStrings( LutecePlutoConstant.CONFIG_SERVICES_PARAM_SUPPORTED_PORTLETMODE );

        for ( int i = 0; i < supportedModes.length; i++ )
        {
            if ( supportedModes[i].equalsIgnoreCase( mode.toString(  ) ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
         * @see org.apache.pluto.services.information.DynamicInformationProvider#isWindowStateAllowed(javax.portlet.WindowState)
         */
    public boolean isWindowStateAllowed( WindowState state )
    {
        //checks whether WindowState is supported as example
        String[] supportedStates = Config.getParameters(  )
                                         .getStrings( LutecePlutoConstant.CONFIG_SERVICES_PARAM_SUPPORTED_WINDOWSTATE );

        for ( int i = 0; i < supportedStates.length; i++ )
        {
            if ( supportedStates[i].equalsIgnoreCase( state.toString(  ) ) )
            {
                return true;
            }
        }

        return false;
    }
}
