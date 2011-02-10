/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
package fr.paris.lutece.plugins.jsr168.pluto.core;

import fr.paris.lutece.plugins.jsr168.pluto.LutecePlutoConstant;
import fr.paris.lutece.portal.web.constants.Parameters;

import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.portalImpl.om.window.impl.PortletWindowImpl;
import org.apache.pluto.portalImpl.services.config.Config;
import org.apache.pluto.services.information.PortletURLProvider;

import java.util.Collections;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Build an URL for a portlet action (action or render)<br>
 *
 * Portlet right's about portlet mode and window state isn't controled.
 */
public class PortletURLProviderImpl implements PortletURLProvider
{
    private static final String _hostNameHTTP;
    private static final String _hostNameHTTPS;

    static
    {
        final String hostName = Config.getParameters(  )
                                      .getString( LutecePlutoConstant.CONFIG_SERVICES_PARAM_HOST_NAME,
                LutecePlutoConstant.CONFIG_SERVICES_PARAM_HOST_NAME_DEFAULT );
        final String hostPortHTTP = Config.getParameters(  )
                                          .getString( LutecePlutoConstant.CONFIG_SERVICES_PARAM_HOST_PORT_HTTP,
                LutecePlutoConstant.CONFIG_SERVICES_PARAM_HOST_PORT_HTTP_DEFAULT );
        final String hostPortHTTPS = Config.getParameters(  )
                                           .getString( LutecePlutoConstant.CONFIG_SERVICES_PARAM_HOST_PORT_HTTPS,
                LutecePlutoConstant.CONFIG_SERVICES_PARAM_HOST_PORT_HTTPS_DEFAULT );

        final StringBuffer hostHTTP = new StringBuffer( "http://" );
        hostHTTP.append( hostName );

        if ( !"80".equals( hostPortHTTP ) )
        {
            hostHTTP.append( ':' );
            hostHTTP.append( hostPortHTTP );
        }

        _hostNameHTTP = hostHTTP.toString(  );

        final StringBuffer hostHTTPS = new StringBuffer( "https://" );
        hostHTTPS.append( hostName );

        if ( !"443".equals( hostPortHTTPS ) )
        {
            hostHTTPS.append( ':' );
            hostHTTPS.append( hostPortHTTPS );
        }

        _hostNameHTTPS = hostHTTPS.toString(  );
    }

    private final HttpServletRequest _request;
    private final DynamicInformationProviderImpl _provider;
    private final PortletWindowImpl _portletWindow;

    /**
     * Mode qui doit apparaître dans l'URL de la portlet.
     */
    private PortletMode _mode;

    /**
     * Etat qui doit apparaître dans l'URL de la portlet.
     */
    private WindowState _state;

    /**
     * Indique que l'URL est de type action (sinon elle est de type
     * render).
     */
    private boolean _bAction;

    /**
     * Indique que l'URL doit être de type sécurisé (HTTPS par exemple).
     */
    private boolean _secure;
    private boolean _clearParameters;
    private Map _parameters;

    /**
     * Initialize a portlet URL provider instance
     *
     * @param request Current HTTP request
     * @param provider Dynamic information provider (for current request)
     * @param portletWindow Current portlet window
     */
    public PortletURLProviderImpl( HttpServletRequest request, DynamicInformationProviderImpl provider,
        PortletWindow portletWindow )
    {
        _request = request;
        _provider = provider;
        _portletWindow = (PortletWindowImpl) portletWindow;
        _bAction = false;
        _secure = false;
    }

    /**
     * @see org.apache.pluto.services.information.PortletURLProvider#setPortletMode(javax.portlet.PortletMode)
     */
    public void setPortletMode( PortletMode mode )
    {
        _mode = mode;
    }

    /**
     * @see org.apache.pluto.services.information.PortletURLProvider#setWindowState(javax.portlet.WindowState)
     */
    public void setWindowState( WindowState state )
    {
        _state = state;
    }

    /**
     * @see org.apache.pluto.services.information.PortletURLProvider#setAction()
     */
    public void setAction(  )
    {
        _bAction = true;
    }

    /**
     * @see org.apache.pluto.services.information.PortletURLProvider#setSecure()
     */
    public void setSecure(  )
    {
        _secure = true;
    }

    /**
     * @see org.apache.pluto.services.information.PortletURLProvider#clearParameters()
     */
    public void clearParameters(  )
    {
        _clearParameters = true;
    }

    /**
         * @see org.apache.pluto.services.information.PortletURLProvider#setParameters(java.util.Map)
         */
    public void setParameters( Map parameters )
    {
        _parameters = parameters;
    }

    /**
         * @see java.lang.Object#toString()
         */
    public String toString(  )
    {
        StringBuffer urlBuf = new StringBuffer(  );

        urlBuf.append( _secure ? _hostNameHTTPS : _hostNameHTTP );
        urlBuf.append( _request.getContextPath(  ) );
        urlBuf.append( LutecePlutoConstant.URL_JSR168_ACTION );

        // Vestige du portail de Pluto: les parametres sont
        // conservés dans la session (et plus dans le PathInfo),
        // l'url est entiérement reconstruite.
        /*
        if ( _clearParameters )
        {
        }
        */
        String params = PortalURL.buildParams( _portletWindow.getId(  ), _bAction, _mode, _state, _parameters );

        if ( ( params != null ) && ( params.length(  ) > 0 ) )
        {
            urlBuf.append( params );
        }

        // /////////////////////////////////////////////////////
        // 
        //    Lutece parameter's
        // 
        // /////////////////////////////////////////////////////        
        if ( _request.getParameter( Parameters.PAGE_ID ) != null )
        {
            urlBuf.append( '&' );
            urlBuf.append( Parameters.PAGE_ID );
            urlBuf.append( '=' );
            urlBuf.append( _request.getParameter( Parameters.PAGE_ID ) );
        }

        PortalEnvironment environment = PortalEnvironment.getPortalEnvironment( _request );

        return environment.getResponse(  ).encodeURL( urlBuf.toString(  ) );
    }

    /**
     * Return the URL base for all URL construction (protocol and server name,
     * for example <code>&quot;http://localhost/lutece&quot;</code>)
     *
     * @return the URL base for all URL construction
     */
    static String getBaseURLexcludeContext(  )
    {
        return _hostNameHTTP;
    }

    /**
     * Return an URL string for redirect user to Lutece portal (used after action request for
     * process render)
     *
     * @param request Current HTTP request
     * @param response Current HTTP response
     * @return an URL string for redirect user to Lutece portal
     */
    public static String getRedirectPortalURL( HttpServletRequest request, final HttpServletResponse response )
    {
        StringBuffer urlBuf = new StringBuffer(  );

        urlBuf.append( _hostNameHTTP );
        urlBuf.append( request.getContextPath(  ) );
        urlBuf.append( LutecePlutoConstant.URL_LUTECE_PORTAL );

        // /////////////////////////////////////////////////////
        // 
        //    Lutece parameter's
        // 
        // /////////////////////////////////////////////////////        
        String pageIdParam = request.getParameter( Parameters.PAGE_ID );

        if ( pageIdParam != null )
        {
            urlBuf.append( '&' );
            urlBuf.append( Parameters.PAGE_ID );
            urlBuf.append( '=' );
            urlBuf.append( pageIdParam );
        }

        return response.encodeURL( urlBuf.toString(  ) );
    }

    /**
     * Return an URL string making a render request
     *
     * @param request Current HTTP request
     * @param response Current HTTP response
     * @param mode Portlet mode for URL
     * @param state Window state for URL
     * @param portletID Portlet ID
     * @return an URL string making a render request
     */
    public static String getRenderURL( HttpServletRequest request, HttpServletResponse response, PortletMode mode,
        WindowState state, ObjectID portletID )
    {
        StringBuffer urlBuf = new StringBuffer(  );

        urlBuf.append( _hostNameHTTP );
        urlBuf.append( request.getContextPath(  ) );
        urlBuf.append( LutecePlutoConstant.URL_JSR168_ACTION );

        String params = PortalURL.buildParams( portletID, false, mode, state, Collections.EMPTY_MAP );

        if ( ( params != null ) && ( params.length(  ) > 0 ) )
        {
            urlBuf.append( params );
        }

        // /////////////////////////////////////////////////////
        // 
        //    Parametre propre à Lutece
        // 
        // /////////////////////////////////////////////////////
        String pageIdParam = request.getParameter( Parameters.PAGE_ID );

        if ( pageIdParam != null )
        {
            urlBuf.append( '&' );
            urlBuf.append( Parameters.PAGE_ID );
            urlBuf.append( '=' );
            urlBuf.append( pageIdParam );
        }

        return response.encodeURL( urlBuf.toString(  ) );
    }
}
